package com.microservice.triage.application.clients;

import com.microservice.triage.application.dto.ProfileSnapshotDTO;

import java.util.Optional;

/**
 * Client interface for Profile microservice.
 */
public interface ProfileClient {

    /**
     * Gets a profile snapshot for a specific user.
     *
     * @param userId The user ID
     * @return The profile snapshot if found
     */
    Optional<ProfileSnapshotDTO> getProfileByUserId(Long userId);
}

