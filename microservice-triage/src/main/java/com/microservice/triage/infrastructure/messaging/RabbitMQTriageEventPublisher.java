package com.microservice.triage.infrastructure.messaging;

import com.microservice.triage.application.events.TriageEventPublisher;
import com.microservice.triage.application.events.TriageResultCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ implementation of TriageEventPublisher.
 */
@Component
public class RabbitMQTriageEventPublisher implements TriageEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQTriageEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQTriageEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishTriageResultCreated(TriageResultCreatedEvent event) {
        try {
            logger.info("📤 Publishing TriageResultCreatedEvent to RabbitMQ - Triage ID: {}, Priority: {}",
                       event.getTriageId(), event.getPriority());

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRIAGE_EXCHANGE,
                RabbitMQConfig.TRIAGE_RESULT_CREATED_ROUTING_KEY,
                event
            );

            logger.debug("✅ TriageResultCreatedEvent published successfully");

        } catch (Exception e) {
            logger.error("❌ Error publishing TriageResultCreatedEvent for triage ID: {}",
                        event.getTriageId(), e);
            throw new RuntimeException("Failed to publish triage event", e);
        }
    }
}

