package com.microservice.casedesk.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.casedesk.application.events.TriageResultCreatedEvent;
import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.events.CaseCreatedEvent;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CaseEventsConsumer {

    private final CaseRepository caseRepository;
    private final CaseEventPublisher caseEventPublisher;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("🚀 CaseEventsConsumer initialized");
    }

    @Bean
    public Consumer<String> anamnesisSummaryCreated() {
        log.info("🔧 Registering anamnesisSummaryCreated consumer");
        return message -> {
            log.info("📥 ANAMNESIS MESSAGE: {}", message);
        };
    }

    @Bean
    public Consumer<String> triageResultCreated() {
        log.info("🔧 Registering triageResultCreated consumer");
        return message -> {
            try {
                log.info("📥 RAW TRIAGE MESSAGE: {}", message);
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
                log.info("✅ CASE CREATED: ID={}, UserID={}", savedCase.getId(), event.getUserId());

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
                log.error("❌ ERROR: {}", e.getMessage(), e);
            }
        };
    }
}
