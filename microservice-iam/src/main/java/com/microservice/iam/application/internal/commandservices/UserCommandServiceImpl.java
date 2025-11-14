package com.microservice.iam.application.internal.commandservices;

import com.microservice.iam.application.internal.outboundservices.hashing.HashingService;
import com.microservice.iam.application.internal.outboundservices.tokens.TokenService;
import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.domain.model.commands.*;
import com.microservice.iam.domain.model.events.*;
import com.microservice.iam.domain.model.valueobjects.Roles;
import com.microservice.iam.domain.model.valueobjects.UserStatus;
import com.microservice.iam.domain.services.UserCommandService;
import com.microservice.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.microservice.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserCommandServiceImpl - Application service for AylluCare/B4U platform.
 * <p>
 *     Implements user command handling with domain event publishing to RabbitMQ.
 *     Coordinates between domain aggregates, repositories, and infrastructure services.
 * </p>
 */
@Service
@Transactional
public class UserCommandServiceImpl implements UserCommandService {
    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private static final String IAM_EVENTS_EXCHANGE = "iam-events";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final StreamBridge streamBridge;

    /**
     * Constructor with dependency injection.
     */
    public UserCommandServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            HashingService hashingService,
            TokenService tokenService,
            StreamBridge streamBridge) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.streamBridge = streamBridge;
    }

    // ========== AylluCare-specific Registration Commands ==========

    @Override
    public Optional<User> handle(RegisterPatientCommand command) {
        log.info("Registering patient with email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists: " + command.email());
        }

        // Get ROLE_PATIENT
        var patientRole = roleRepository.findByName(Roles.ROLE_PATIENT)
                .orElseThrow(() -> new RuntimeException("ROLE_PATIENT not found in database"));

        // Create user with ACTIVE status
        var user = new User(
            command.firstName(),
            command.lastName(),
            command.email(),
            hashingService.encode(command.password()),
            List.of(patientRole),
            command.phoneNumber(),
            command.preferredLanguage()
        );

        var savedUser = userRepository.save(user);
        log.info("Patient registered successfully with userId: {}", savedUser.getId());

        // Publish domain events
        publishUserCreatedEvent(savedUser);
        publishUserRegisteredAsPatientEvent(savedUser, command);

        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(RegisterDoctorCommand command) {
        log.info("Registering doctor with email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists: " + command.email());
        }

        // Get ROLE_DOCTOR
        var doctorRole = roleRepository.findByName(Roles.ROLE_DOCTOR)
                .orElseThrow(() -> new RuntimeException("ROLE_DOCTOR not found in database"));

        // Create user (INACTIVE if requires verification)
        var user = new User(
            command.firstName(),
            command.lastName(),
            command.email(),
            hashingService.encode(command.password()),
            List.of(doctorRole),
            command.phoneNumber(),
            "es" // Default language for doctors
        );

        if (command.requiresVerification()) {
            user.setStatus(UserStatus.INACTIVE);
            log.info("Doctor account created as INACTIVE pending verification");
        }

        var savedUser = userRepository.save(user);
        log.info("Doctor registered successfully with userId: {}", savedUser.getId());

        // Publish domain events
        publishUserCreatedEvent(savedUser);
        publishUserRegisteredAsDoctorEvent(savedUser, command);

        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        log.info("Generic user registration with email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists: " + command.email());
        }

        var roles = command.roles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getName())))
                .toList();

        var user = new User(
            command.firstName(),
            command.lastName(),
            command.email(),
            hashingService.encode(command.password()),
            roles,
            command.phoneNumber(),
            command.preferredLanguage()
        );

        var savedUser = userRepository.save(user);
        log.info("User registered successfully with userId: {}", savedUser.getId());

        publishUserCreatedEvent(savedUser);

        return Optional.of(savedUser);
    }


    // ========== Authentication Commands ==========

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        log.info("Sign in attempt for email: {}", command.username());

        var user = userRepository.findByEmail(command.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Validate password
        if (!hashingService.matches(command.password(), user.getPassword())) {
            log.warn("Invalid password attempt for email: {}", command.username());
            throw new RuntimeException("Invalid credentials");
        }

        // Validate user status (important for doctor verification)
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Login attempt for non-active user: {}, status: {}", user.getId(), user.getStatus());
            throw new RuntimeException("Account is " + user.getStatus().name().toLowerCase());
        }

        // Generate JWT token (valid for 7 days by default, configurable)
        var token = tokenService.generateToken(user.getEmail());

        log.info("User {} logged in successfully", user.getId());

        // Publish UserLoggedInEvent (optional, for analytics)
        publishUserLoggedInEvent(user);

        return Optional.of(new ImmutablePair<>(user, token));
    }


    // ========== User Management Commands ==========

    @Override
    public Optional<User> handle(UpdateUserRolesCommand command) {
        log.info("Updating roles for user: {} by admin: {}", command.userId(), command.adminUserId());

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store previous roles for event
        var previousRoles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        // Get new roles from repository
        var newRoles = command.newRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .toList();

        // Update roles using domain method
        user.updateRoles(newRoles);
        var savedUser = userRepository.save(user);

        // Publish UserRoleChangedEvent
        var newRoleNames = savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        publishUserRoleChangedEvent(savedUser, previousRoles, newRoleNames, command.adminUserId());

        log.info("Roles updated successfully for user: {}", savedUser.getId());
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(UpdateUserStatusCommand command) {
        log.info("Updating status for user: {} to {} by admin: {}",
                command.userId(), command.newStatus(), command.adminUserId());

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var previousStatus = user.getStatus();

        // Update status using domain method
        user.updateStatus(command.newStatus());
        var savedUser = userRepository.save(user);

        // Publish UserStatusChangedEvent
        publishUserStatusChangedEvent(
                savedUser,
                previousStatus.name(),
                command.newStatus().name(),
                command.reason(),
                command.adminUserId()
        );

        log.info("Status updated successfully for user: {} from {} to {}",
                savedUser.getId(), previousStatus, command.newStatus());
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(ChangePasswordCommand command) {
        log.info("Password change request for user: {}", command.userId());

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!hashingService.matches(command.currentPassword(), user.getPassword())) {
            log.warn("Incorrect current password for user: {}", command.userId());
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(hashingService.encode(command.newPassword()));
        var savedUser = userRepository.save(user);

        log.info("Password changed successfully for user: {}", savedUser.getId());
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(ChangeEmailCommand command) {
        log.info("Email change request for user: {}", command.userId());

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!hashingService.matches(command.password(), user.getPassword())) {
            log.warn("Incorrect password for email change, user: {}", command.userId());
            throw new RuntimeException("Password is incorrect");
        }

        if (userRepository.existsByEmail(command.newEmail())) {
            throw new RuntimeException("Email already exists: " + command.newEmail());
        }

        user.setEmail(command.newEmail());
        var savedUser = userRepository.save(user);

        log.info("Email changed successfully for user: {}", savedUser.getId());
        return Optional.of(savedUser);
    }

    @Override
    public boolean deleteUser(Long userId) {
        log.info("Attempting to delete user with ID: {}", userId);

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("User with ID {} not found", userId);
            return false;
        }

        try {
            userRepository.deleteById(userId);
            log.info("User with ID {} deleted successfully", userId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", userId, e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }


    // ========== Event Publishing Methods ==========

    private void publishUserCreatedEvent(User user) {
        try {
            var roleNames = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());

            UserCreatedEvent event = new UserCreatedEvent(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    roleNames,
                    user.getStatus().name(),
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserCreatedEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserCreatedEvent for userId: {}", user.getId(), e);
        }
    }

    private void publishUserRegisteredAsPatientEvent(User user, RegisterPatientCommand command) {
        try {
            UserRegisteredAsPatientEvent event = new UserRegisteredAsPatientEvent(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    command.phoneNumber(),
                    command.preferredLanguage(),
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserRegisteredAsPatientEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredAsPatientEvent for userId: {}", user.getId(), e);
        }
    }

    private void publishUserRegisteredAsDoctorEvent(User user, RegisterDoctorCommand command) {
        try {
            UserRegisteredAsDoctorEvent event = new UserRegisteredAsDoctorEvent(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    command.phoneNumber(),
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserRegisteredAsDoctorEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredAsDoctorEvent for userId: {}", user.getId(), e);
        }
    }

    private void publishUserRoleChangedEvent(User user, Set<String> previousRoles,
                                            Set<String> newRoles, Long changedBy) {
        try {
            UserRoleChangedEvent event = new UserRoleChangedEvent(
                    user.getId(),
                    user.getEmail(),
                    previousRoles,
                    newRoles,
                    changedBy,
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserRoleChangedEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserRoleChangedEvent for userId: {}", user.getId(), e);
        }
    }

    private void publishUserStatusChangedEvent(User user, String previousStatus,
                                               String newStatus, String reason, Long changedBy) {
        try {
            UserStatusChangedEvent event = new UserStatusChangedEvent(
                    user.getId(),
                    user.getEmail(),
                    previousStatus,
                    newStatus,
                    reason,
                    changedBy,
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserStatusChangedEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserStatusChangedEvent for userId: {}", user.getId(), e);
        }
    }

    private void publishUserLoggedInEvent(User user) {
        try {
            var roleNames = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());

            UserLoggedInEvent event = new UserLoggedInEvent(
                    user.getId(),
                    user.getEmail(),
                    roleNames,
                    null, // IP address - can be passed from controller if needed
                    null, // User agent - can be passed from controller if needed
                    Instant.now()
            );

            streamBridge.send(IAM_EVENTS_EXCHANGE, event);
            log.info("UserLoggedInEvent published for userId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserLoggedInEvent for userId: {}", user.getId(), e);
        }
    }
}
