package com.microservice.iam.domain.services;

import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.domain.model.commands.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

/**
 * UserCommandService - Domain service for AylluCare/B4U platform.
 * <p>
 *     Handles user-related commands including registration, authentication,
 *     and user management operations.
 * </p>
 */
public interface UserCommandService {

    // ========== AylluCare-specific Registration Commands ==========

    /**
     * Handle patient registration command.
     *
     * @param command the RegisterPatientCommand
     * @return an optional of User if registration was successful
     */
    Optional<User> handle(RegisterPatientCommand command);

    /**
     * Handle doctor registration command.
     *
     * @param command the RegisterDoctorCommand
     * @return an optional of User if registration was successful
     */
    Optional<User> handle(RegisterDoctorCommand command);

    // ========== Generic Registration (kept for backward compatibility) ==========

    /**
     * Handle generic sign-up command.
     *
     * @param command the SignUpCommand
     * @return an optional of User if the sign-up was successful
     */
    Optional<User> handle(SignUpCommand command);

    // ========== Authentication Commands ==========

    /**
     * Handle sign in command.
     *
     * @param command the SignInCommand
     * @return an optional pair of User and JWT token if sign-in was successful
     */
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);

    // ========== User Management Commands ==========

    /**
     * Handle update user roles command.
     *
     * @param command the UpdateUserRolesCommand
     * @return an optional of User if roles were updated successfully
     */
    Optional<User> handle(UpdateUserRolesCommand command);

    /**
     * Handle update user status command.
     *
     * @param command the UpdateUserStatusCommand
     * @return an optional of User if status was updated successfully
     */
    Optional<User> handle(UpdateUserStatusCommand command);

    /**
     * Handle change password command.
     *
     * @param command the ChangePasswordCommand
     * @return an optional of User if password change was successful
     */
    Optional<User> handle(ChangePasswordCommand command);

    /**
     * Handle change email command.
     *
     * @param command the ChangeEmailCommand
     * @return an optional of User if email change was successful
     */
    Optional<User> handle(ChangeEmailCommand command);

    /**
     * Delete a user by ID.
     *
     * @param userId the ID of the user to delete
     * @return true if the user was deleted successfully
     */
    boolean deleteUser(Long userId);
}
