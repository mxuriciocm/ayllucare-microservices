package com.microservice.iam.application.internal.queryservices;

import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.domain.model.queries.*;
import com.microservice.iam.domain.model.valueobjects.Roles;
import com.microservice.iam.domain.model.valueobjects.UserStatus;
import com.microservice.iam.domain.services.UserQueryService;
import com.microservice.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserQueryServiceImpl for AylluCare/B4U platform.
 * <p>
 *     Implementation of user query service with support for filtering by role and status.
 * </p>
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param userRepository {@link UserRepository} instance
     */
    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public List<User> handle(GetUsersByRoleQuery query) {
        // Convert string to Roles enum
        Roles role = Roles.valueOf(query.roleName());

        // Get all users and filter by role
        return userRepository.findAll().stream()
            .filter(user -> user.getRoles().stream()
                .anyMatch(r -> r.getName() == role))
            .collect(Collectors.toList());
    }

    @Override
    public List<User> handle(GetUsersByStatusQuery query) {
        // Convert string to UserStatus enum
        UserStatus status = UserStatus.valueOf(query.status());

        // Get all users and filter by status
        return userRepository.findAll().stream()
            .filter(user -> user.getStatus() == status)
            .collect(Collectors.toList());
    }
}
