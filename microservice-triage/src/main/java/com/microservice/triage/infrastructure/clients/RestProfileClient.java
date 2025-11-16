package com.microservice.triage.infrastructure.clients;

import com.microservice.triage.application.clients.ProfileClient;
import com.microservice.triage.application.dto.ProfileSnapshotDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * REST client for Profile microservice.
 */
@Component
public class RestProfileClient implements ProfileClient {

    private static final Logger logger = LoggerFactory.getLogger(RestProfileClient.class);

    private final WebClient webClient;

    @Value("${services.profile.url:http://microservice-profiles}")
    private String profileServiceUrl;

    public RestProfileClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Optional<ProfileSnapshotDTO> getProfileByUserId(Long userId) {
        logger.debug("Fetching profile for userId: {}", userId);

        try {
            ProfileSnapshotDTO profile = webClient.get()
                    .uri(profileServiceUrl + "/api/v1/profiles/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(ProfileSnapshotDTO.class)
                    .block();

            logger.debug("Profile retrieved successfully for userId: {}", userId);
            return Optional.ofNullable(profile);

        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Profile not found for userId: {}", userId);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error fetching profile for userId: {}", userId, e);
            return Optional.empty();
        }
    }
}

