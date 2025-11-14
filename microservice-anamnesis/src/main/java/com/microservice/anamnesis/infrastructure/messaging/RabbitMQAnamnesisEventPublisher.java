package com.microservice.anamnesis.infrastructure.messaging;

import com.microservice.anamnesis.application.events.AnamnesisEventPublisher;
import com.microservice.anamnesis.domain.model.events.AnamnesisSessionCompletedEvent;
import com.microservice.anamnesis.domain.model.events.AnamnesisSummaryCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Implementation of AnamnesisEventPublisher using Spring Cloud Stream and RabbitMQ.
 */
@Component
public class RabbitMQAnamnesisEventPublisher implements AnamnesisEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQAnamnesisEventPublisher.class);
    private static final String SUMMARY_CREATED_BINDING = "anamnesisSummaryCreated-out-0";
    private static final String SESSION_COMPLETED_BINDING = "anamnesisSessionCompleted-out-0";

    private final StreamBridge streamBridge;

    public RabbitMQAnamnesisEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publishAnamnesisSummaryCreated(AnamnesisSummaryCreatedEvent event) {
        try {
            logger.info("Publishing AnamnesisSummaryCreatedEvent for session: {}", event.getSessionId());
            streamBridge.send(SUMMARY_CREATED_BINDING, event);
            logger.debug("Event published successfully: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Error publishing AnamnesisSummaryCreatedEvent", e);
            throw new RuntimeException("Failed to publish anamnesis summary created event", e);
        }
    }

    @Override
    public void publishAnamnesisSessionCompleted(AnamnesisSessionCompletedEvent event) {
        try {
            logger.info("Publishing AnamnesisSessionCompletedEvent for session: {}", event.getSessionId());
            streamBridge.send(SESSION_COMPLETED_BINDING, event);
            logger.debug("Event published successfully: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Error publishing AnamnesisSessionCompletedEvent", e);
            throw new RuntimeException("Failed to publish anamnesis session completed event", e);
        }
    }
}

