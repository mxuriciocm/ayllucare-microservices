package com.microservice.triage.application.internal.eventhandlers;

import com.microservice.triage.application.clients.ProfileClient;
import com.microservice.triage.application.dto.ProfileSnapshotDTO;
import com.microservice.triage.application.events.AnamnesisSummaryCreatedEvent;
import com.microservice.triage.application.events.TriageResultCreatedEvent;
import com.microservice.triage.domain.model.commands.CreateTriageResultCommand;
import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import com.microservice.triage.domain.services.TriageCommandService;
import com.microservice.triage.domain.services.TriageDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Configuration
public class AnamnesisSummaryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AnamnesisSummaryEventHandler.class);

    private final TriageCommandService triageCommandService;
    private final TriageDomainService triageDomainService;
    private final ProfileClient profileClient;
    private final StreamBridge streamBridge;

    public AnamnesisSummaryEventHandler(TriageCommandService triageCommandService,
                                        TriageDomainService triageDomainService,
                                        ProfileClient profileClient,
                                        StreamBridge streamBridge) {
        this.triageCommandService = triageCommandService;
        this.triageDomainService = triageDomainService;
        this.profileClient = profileClient;
        this.streamBridge = streamBridge;
    }

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
            }
        };
    }

    private void processAnamnesisSummary(AnamnesisSummaryCreatedEvent event) {
        Optional<ProfileSnapshotDTO> profileOpt = profileClient.getProfileByUserId(event.getUserId());
        ProfileSnapshotDTO profile = profileOpt.orElse(null);

        PriorityLevel priority = triageDomainService.calculatePriority(event.getSummary(), profile);
        List<String> riskFactors = triageDomainService.identifyRiskFactors(event.getSummary(), profile);
        List<String> redFlags = event.getSummary().getRedFlags();
        String recommendations = triageDomainService.generateRecommendations(priority, event.getSummary(), profile);

        CreateTriageResultCommand command = new CreateTriageResultCommand(
                event.getUserId(),
                event.getSessionId(),
                priority,
                riskFactors,
                redFlags,
                recommendations
        );

        var result = triageCommandService.handle(command);

        if (result.isPresent()) {
            var triageResult = result.get();

            logger.info("✅ Triage result created successfully with ID: {} and priority: {}",
                    triageResult.getId(), triageResult.getPriority());

            // 🚀 PUBLICAR EL EVENTO
            TriageResultCreatedEvent triageEvent = new TriageResultCreatedEvent(
                    triageResult.getId(),
                    triageResult.getUserId(),
                    triageResult.getSessionId(),
                    triageResult.getPriority(),
                    riskFactors,
                    redFlags,
                    recommendations,
                    event.getSummary() != null ? event.getSummary().getChiefComplaint() : null
            );

            Message<TriageResultCreatedEvent> message = MessageBuilder
                    .withPayload(triageEvent)
                    .setHeader("routingKey", "triage.result.created")
                    .build();

            boolean sent = streamBridge.send("triageResultCreated-out-0", message);

            if (sent) {
                logger.info("🚀 Published TriageResultCreatedEvent for userId: {} with priority: {}",
                        triageResult.getUserId(), triageResult.getPriority());
            } else {
                logger.error("❌ Failed to publish TriageResultCreatedEvent");
            }

            if (triageResult.isEmergency()) {
                logger.warn("🚨 EMERGENCY CASE DETECTED! Triage ID: {}, User ID: {}",
                        triageResult.getId(), triageResult.getUserId());
            }
        }
    }
}
