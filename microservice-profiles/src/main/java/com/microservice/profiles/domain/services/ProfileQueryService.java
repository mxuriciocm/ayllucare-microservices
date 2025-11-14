package com.microservice.profiles.domain.services;

import com.microservice.profiles.domain.model.aggregates.Profile;
import com.microservice.profiles.domain.model.queries.GetAllProfilesQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByIdQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * User profile query service for AylluCare.
 * <p>
 *     This service is responsible for handling user profile queries.
 *     It provides methods to handle queries for getting a user profile by ID, by user ID (from IAM), and for getting all user profiles.
 * </p>
 */
public interface ProfileQueryService {
    /**
     * Handle get user profile by profile ID query.
     *
     * @param query the query containing the profile ID
     * @return an optional of Profile if found
     */
    Optional<Profile> handle(GetProfileByIdQuery query);

    /**
     * Handle get user profile by user ID query.
     * This retrieves the profile associated with a user from IAM service
     *
     * @param query the query containing the user ID from IAM
     * @return an optional of Profile if found
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);

    /**
     * Handle get all user profiles queries.
     *
     * @param query the query to get all user profiles
     * @return a list of Profile
     */
    List<Profile> handle(GetAllProfilesQuery query);
}