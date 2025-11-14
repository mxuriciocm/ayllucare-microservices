package com.microservice.iam.domain.model.aggregates;

import com.microservice.iam.domain.model.entities.Role;
import com.microservice.iam.domain.model.valueobjects.UserStatus;
import com.microservice.iam.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root for AylluCare/B4U platform.
 * <p>
 *     Represents a user in the rural digital health platform.
 *     Users can be patients, doctors, or administrators.
 *     This aggregate is responsible for managing identity, credentials, and authorization.
 * </p>
 * <p>
 *     Domain invariants:
 *     - email must be unique
 *     - at least 1 role is required
 *     - new users are ACTIVE by default
 *     - passwords must be stored as secure hashes
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Size(max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 5)
    @Column(name = "preferred_language")
    private String preferredLanguage; // e.g., "es", "qu", "en"

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    /**
     * Default constructor.
     * Initializes user with ACTIVE status and empty roles set.
     */
    public User() {
        this.roles = new HashSet<>();
        this.status = UserStatus.ACTIVE;
        this.preferredLanguage = "es"; // Default to Spanish for rural Peru
    }

    /**
     * Constructor for creating a new user in AylluCare/B4U.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email (must be unique)
     * @param passwordHash the hashed password
     * @param roles the list of roles to assign
     */
    public User(String firstName, String lastName, String email, String passwordHash, List<Role> roles) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = passwordHash;
        addRoles(roles);
    }

    /**
     * Constructor with optional fields.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email
     * @param passwordHash the hashed password
     * @param roles the list of roles
     * @param phoneNumber the user's phone number (optional)
     * @param preferredLanguage the user's preferred language (optional)
     */
    public User(String firstName, String lastName, String email, String passwordHash,
                List<Role> roles, String phoneNumber, String preferredLanguage) {
        this(firstName, lastName, email, passwordHash, roles);
        this.phoneNumber = phoneNumber;
        if (preferredLanguage != null && !preferredLanguage.isBlank()) {
            this.preferredLanguage = preferredLanguage;
        }
    }

    /**
     * Add a role to the user.
     *
     * @param role the role to add
     * @return the {@link User} instance
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /**
     * Add a set of roles to the user.
     * Validates that at least one role is provided (domain invariant).
     *
     * @param roles the roles to add
     * @return the {@link User} instance
     */
    public User addRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
        var validatedRoles = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoles);
        return this;
    }

    /**
     * Update user roles. Replaces existing roles with new ones.
     *
     * @param newRoles the new set of roles
     */
    public void updateRoles(List<Role> newRoles) {
        if (newRoles == null || newRoles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
        this.roles.clear();
        addRoles(newRoles);
    }

    /**
     * Update user status (ACTIVE, INACTIVE, LOCKED).
     *
     * @param newStatus the new status
     */
    public void updateStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
    }

    /**
     * Check if user is active.
     *
     * @return true if user status is ACTIVE
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * Check if user has a specific role.
     *
     * @param roleName the role name to check
     * @return true if user has the role
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().name().equals(roleName));
    }

    /**
     * Get full name.
     *
     * @return the full name (firstName + lastName)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
