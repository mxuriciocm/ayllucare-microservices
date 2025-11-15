package com.microservice.triage.application.internal.eventhandlers;

import com.microservice.triage.application.clients.ProfileClient;
import com.microservice.triage.application.dto.ProfileSnapshotDTO;
import com.microservice.triage.application.events.AnamnesisSummaryCreatedEvent;
import com.microservice.triage.domain.model.commands.CreateTriageResultCommand;
import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import com.microservice.triage.domain.services.TriageCommandService;
import com.microservice.triage.domain.services.TriageDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Event handler for consuming AnamnesisSummaryCreatedEvent from RabbitMQ.
 * Processes anamnesis summaries and creates triage results.
 */
@Configuration
public class AnamnesisSummaryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AnamnesisSummaryEventHandler.class);

    private final TriageCommandService triageCommandService;
    private final TriageDomainService triageDomainService;
    private final ProfileClient profileClient;

    public AnamnesisSummaryEventHandler(TriageCommandService triageCommandService,
                                       TriageDomainService triageDomainService,
                                       ProfileClient profileClient) {
        this.triageCommandService = triageCommandService;
        this.triageDomainService = triageDomainService;
        this.profileClient = profileClient;
    }

    /**
     * Spring Cloud Stream consumer for AnamnesisSummaryCreatedEvent.
     * Function name must match the binding configuration in application.properties.
     */
    @Bean
    public Consumer<AnamnesisSummaryCreatedEvent> anamnesisSummaryCreated() {
        return event -> {
            try {
                logger.info("📩 Received AnamnesisSummaryCreatedEvent for userId: {}, sessionId: {}",
                           event.getUserId(), event.getSessionId());

                processAnamnesisSummary(event);

            } catch (Exception e) {
                logger.error("❌ Error processing AnamnesisSummaryCreatedEvent for sessionId: {}",
                            event.getSessionId(), e);
                // In production, consider implementing retry logic or dead letter queue
            }
        };
    }

    private void processAnamnesisSummary(AnamnesisSummaryCreatedEvent event) {
        // 1. Get patient profile from Profile microservice
        logger.debug("Fetching profile for userId: {}", event.getUserId());
        Optional<ProfileSnapshotDTO> profileOpt = profileClient.getProfileByUserId(event.getUserId());

        ProfileSnapshotDTO profile = profileOpt.orElse(null);
        if (profile == null) {
            logger.warn("⚠️ Profile not found for userId: {}, continuing with limited data", event.getUserId());
        } else {
            logger.debug("✅ Profile retrieved successfully for userId: {}", event.getUserId());
        }

        // 2. Calculate priority using domain service
        logger.debug("Calculating priority level...");
        PriorityLevel priority = triageDomainService.calculatePriority(event.getSummary(), profile);
        logger.info("🎯 Priority calculated: {} for sessionId: {}", priority, event.getSessionId());

        // 3. Identify risk factors
        List<String> riskFactors = triageDomainService.identifyRiskFactors(event.getSummary(), profile);
        logger.debug("Identified {} risk factors", riskFactors.size());

        // 4. Get red flags from anamnesis summary
        List<String> redFlags = event.getSummary().getRedFlags();
        if (redFlags != null && !redFlags.isEmpty()) {
            logger.info("⚠️ {} red flags detected", redFlags.size());
        }

        // 5. Generate recommendations
        String recommendations = triageDomainService.generateRecommendations(
            priority, event.getSummary(), profile
        );

        // 6. Create triage result command
        CreateTriageResultCommand command = new CreateTriageResultCommand(
            event.getUserId(),
            event.getSessionId(),
            priority,
            riskFactors,
            redFlags,
            recommendations
        );

        // 7. Execute command to create triage result
        var result = triageCommandService.handle(command);

        if (result.isPresent()) {
            logger.info("✅ Triage result created successfully with ID: {} and priority: {}",
                       result.get().getId(), result.get().getPriority());

            if (result.get().isEmergency()) {
                logger.warn("🚨 EMERGENCY CASE DETECTED! Triage ID: {}, User ID: {}",
                           result.get().getId(), result.get().getUserId());
            }
        } else {
            logger.error("❌ Failed to create triage result for sessionId: {}", event.getSessionId());
        }
    }
}

