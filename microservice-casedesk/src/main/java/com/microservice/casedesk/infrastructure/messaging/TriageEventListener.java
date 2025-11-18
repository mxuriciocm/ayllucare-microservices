package com.microservice.casedesk.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.application.events.TriageResultCreatedEvent;
import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.events.CaseCreatedEvent;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TriageEventListener {

    private final CaseRepository caseRepository;
    private final CaseEventPublisher caseEventPublisher;
    private final ObjectMapper objectMapper;

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("🚀 TriageEventListener initialized - @RabbitListener ready to consume from: triage.events.casedesk-service");
    }

    /**
     * Listener que consume eventos de triage usando @RabbitListener
     * Esto es más directo y confiable que usar Spring Cloud Stream Function
     */
    @RabbitListener(queues = "triage.events.casedesk-service")
    public void handleTriageResultCreated(String message) {
        try {
            log.info("📥 RAW TRIAGE MESSAGE RECEIVED: {}", message);

            TriageResultCreatedEvent event = objectMapper.readValue(message, TriageResultCreatedEvent.class);
            log.info("📋 EVENT PARSED - UserID: {}, TriageLevel: {}", event.getUserId(), event.getTriageLevel());

            TriageLevel triageLevel = TriageLevel.valueOf(event.getTriageLevel().toUpperCase());
            Long triageId = event.getTriageId() != null ? event.getTriageId() : (long) Math.abs(event.getEventId().hashCode());

            Case newCase = new Case(
                    event.getUserId(),
                    triageId,
                    event.getAnamnesisSessionId(),
                    triageLevel,
                    event.getChiefComplaint(),
                    event.getRedFlags(),
                    event.getRecommendedAction()
            );

            Case savedCase = caseRepository.save(newCase);
            log.info("✅ CASE CREATED: ID={}, UserID={}, TriageLevel={}",
                    savedCase.getId(), event.getUserId(), triageLevel);

            CaseCreatedEvent caseEvent = CaseCreatedEvent.create(
                    savedCase.getId(),
                    savedCase.getPatientId(),
                    savedCase.getAnamnesisSessionId(),
                    savedCase.getTriageLevel(),
                    savedCase.getChiefComplaint()
            );
            caseEventPublisher.publish(caseEvent);
            log.info("📤 CASE EVENT PUBLISHED: {}", savedCase.getId());

        } catch (Exception e) {
            log.error("❌ ERROR processing triage event: {}", e.getMessage(), e);
        }
    }
}

