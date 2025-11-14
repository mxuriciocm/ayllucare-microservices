package com.microservice.profiles.domain.services;

import com.microservice.profiles.domain.model.aggregates.Profile;
import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.domain.model.commands.SignConsentCommand;
import com.microservice.profiles.domain.model.commands.UpdateProfileCommand;

import java.util.Optional;

/**
 * UserProfile command service for AylluCare
 */
public interface ProfileCommandService {
    /**
     * Handle Create UserProfile Command
     *
     * @param command The {@link CreateProfileCommand} Command
     * @return An {@link Optional < Profile >} instance if the command is valid, otherwise empty
     * @throws IllegalArgumentException if the userId already has a profile
     */
    Optional<Profile> handle(CreateProfileCommand command);

    /**
     * Handle Update UserProfile Command
     *
     * @param command The {@link UpdateProfileCommand} Command
     * @return An {@link Optional< Profile >} instance if the profile was updated successfully, otherwise empty
     * @throws IllegalArgumentException if the profile doesn't exist
     */
    Optional<Profile> handle(UpdateProfileCommand command);

    /**
     * Handle Sign Consent Command
     * Patient signs consent for data sharing and AI processing (GDPR/HIPAA compliance)
     *
     * @param command The {@link SignConsentCommand} Command
     * @return An {@link Optional< Profile >} instance if consent was signed successfully, otherwise empty
     * @throws IllegalArgumentException if the profile doesn't exist
     */
    Optional<Profile> handle(SignConsentCommand command);
}