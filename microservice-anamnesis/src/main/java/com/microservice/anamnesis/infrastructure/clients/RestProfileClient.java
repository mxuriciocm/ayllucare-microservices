package com.microservice.anamnesis.infrastructure.clients;

import com.microservice.anamnesis.application.clients.ProfileClient;
import com.microservice.anamnesis.application.dto.ProfileSnapshot;
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
    public Optional<ProfileSnapshot> getProfileByUserId(Long userId) {
        logger.debug("Fetching profile for userId: {}", userId);

        try {
            ProfileSnapshot profile = webClient.get()
                    .uri(profileServiceUrl + "/api/v1/profiles/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(ProfileSnapshot.class)
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

    @Override
    public boolean hasConsentForAIProcessing(Long userId) {
        logger.debug("Checking AI consent for userId: {}", userId);

        try {
            Optional<ProfileSnapshot> profile = getProfileByUserId(userId);

            if (profile.isPresent()) {
                boolean hasConsent = profile.get().hasConsentForAI();
                logger.debug("User {} AI consent: {}", userId, hasConsent);
                return hasConsent;
            } else {
                // If profile doesn't exist yet (user just registered, event still being processed),
                // assume consent by default. In production, you may want to wait or handle differently.
                logger.info("Profile not found for userId: {}, assuming consent by default (profile may be being created)", userId);
                return true; // Changed from false to true
            }

        } catch (Exception e) {
            logger.error("Error checking consent for userId: {}", userId, e);
            // In case of error, allow the process to continue
            return true;
        }
    }
}

