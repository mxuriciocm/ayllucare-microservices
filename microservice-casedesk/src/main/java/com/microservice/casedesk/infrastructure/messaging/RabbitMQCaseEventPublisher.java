package com.microservice.casedesk.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.domain.model.events.CaseAssignedEvent;
import com.microservice.casedesk.domain.model.events.CaseCreatedEvent;
import com.microservice.casedesk.domain.model.events.CaseStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQCaseEventPublisher implements CaseEventPublisher {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(CaseCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            streamBridge.send("caseEvents-out-0", message);
            log.info("Published CaseCreatedEvent: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error publishing CaseCreatedEvent", e);
        }
    }

    @Override
    public void publish(CaseAssignedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            streamBridge.send("caseEvents-out-0", message);
            log.info("Published CaseAssignedEvent: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error publishing CaseAssignedEvent", e);
        }
    }

    @Override
    public void publish(CaseStatusChangedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            streamBridge.send("caseEvents-out-0", message);
            log.info("Published CaseStatusChangedEvent: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error publishing CaseStatusChangedEvent", e);
        }
    }
}
