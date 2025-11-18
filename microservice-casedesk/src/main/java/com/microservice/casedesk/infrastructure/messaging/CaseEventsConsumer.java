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

    // Beans Consumer eliminados - Ahora usamos @RabbitListener en TriageEventListener.java
    // Esto evita conflictos entre Spring Cloud Stream y Spring AMQP
}
