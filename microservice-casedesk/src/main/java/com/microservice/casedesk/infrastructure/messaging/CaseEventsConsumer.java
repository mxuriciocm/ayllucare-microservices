package com.microservice.casedesk.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.casedesk.application.events.AnamnesisSummaryCreatedEvent;
import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.application.events.TriageResultCreatedEvent;
import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.events.CaseCreatedEvent;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseEventsConsumer {

    private final CaseRepository caseRepository;
    private final CaseEventPublisher caseEventPublisher;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<String> consumeAnamnesisSummaryCreated() {
        return message -> {
            try {
                AnamnesisSummaryCreatedEvent event = objectMapper.readValue(message, AnamnesisSummaryCreatedEvent.class);
                log.info("Received anamnesis summary created event: {}", event.getEventId());
            } catch (Exception e) {
                log.error("Error processing anamnesis summary created event", e);
            }
        };
    }

    @Bean
    public Consumer<String> consumeTriageResultCreated() {
        return message -> {
            try {
                TriageResultCreatedEvent event = objectMapper.readValue(message, TriageResultCreatedEvent.class);
                log.info("Received triage result created event: {}", event.getEventId());

                TriageLevel triageLevel = TriageLevel.valueOf(event.getTriageLevel().toUpperCase());

                Case newCase = new Case(
                        event.getUserId(),
                        event.getAnamnesisSessionId(),
                        triageLevel,
                        "Chief complaint from triage",
                        List.of(),
                        event.getRecommendedAction()
                );

                Case savedCase = caseRepository.save(newCase);
                log.info("Created new case with id: {}", savedCase.getId());

                CaseCreatedEvent caseCreatedEvent = CaseCreatedEvent.create(
                        savedCase.getId(),
                        savedCase.getPatientId(),
                        savedCase.getAnamnesisSessionId(),
                        savedCase.getTriageLevel(),
                        savedCase.getChiefComplaint()
                );
                caseEventPublisher.publish(caseCreatedEvent);

            } catch (Exception e) {
                log.error("Error processing triage result created event", e);
            }
        };
    }
}
