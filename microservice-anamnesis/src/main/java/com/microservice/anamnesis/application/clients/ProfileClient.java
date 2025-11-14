package com.microservice.anamnesis.application.clients;

import com.microservice.anamnesis.application.dto.ProfileSnapshot;
import java.util.Optional;

/**
 * Client interface for retrieving profile information from Profile microservice.
 */
public interface ProfileClient {

    /**
     * Retrieve profile snapshot for a given user
     */
    Optional<ProfileSnapshot> getProfileByUserId(Long userId);

    /**
     * Check if user has consent for AI processing
     */
    boolean hasConsentForAIProcessing(Long userId);
}


