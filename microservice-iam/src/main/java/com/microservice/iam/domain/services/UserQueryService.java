package com.microservice.iam.domain.services;

import com.microservice.iam.domain.model.aggregates.User;
import com.microservice.iam.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * UserQueryService for AylluCare/B4U platform.
 * <p>
 *     Handles user queries including retrieval by ID, email, role, and status.
 * </p>
 */
public interface UserQueryService {
    /**
     * Handle get all users query.
     *
     * @param query the query
     * @return a list of all users
     */
    List<User> handle(GetAllUsersQuery query);

    /**
     * Handle get user by ID query.
     *
     * @param query the query
     * @return an optional containing the user if found
     */
    Optional<User> handle(GetUserByIdQuery query);

    /**
     * Handle get user by email query.
     *
     * @param query the query
     * @return an optional containing the user if found
     */
    Optional<User> handle(GetUserByEmailQuery query);

    /**
     * Handle get users by role query.
     *
     * @param query the query containing role name
     * @return a list of users with the specified role
     */
    List<User> handle(GetUsersByRoleQuery query);

    /**
     * Handle get users by status query.
     *
     * @param query the query containing status
     * @return a list of users with the specified status
     */
    List<User> handle(GetUsersByStatusQuery query);
}
