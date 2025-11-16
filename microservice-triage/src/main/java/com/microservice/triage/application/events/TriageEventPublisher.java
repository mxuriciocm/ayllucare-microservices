package com.microservice.triage.application.events;

/**
 * Event publisher interface for triage events.
 */
public interface TriageEventPublisher {

    /**
     * Publishes a TriageResultCreatedEvent to RabbitMQ.
     *
     * @param event The event to publish
     */
    void publishTriageResultCreated(TriageResultCreatedEvent event);
}

