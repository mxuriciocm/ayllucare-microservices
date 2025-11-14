package com.microservice.anamnesis.application.events;

import com.microservice.anamnesis.domain.model.events.AnamnesisSessionCompletedEvent;
import com.microservice.anamnesis.domain.model.events.AnamnesisSummaryCreatedEvent;

public interface AnamnesisEventPublisher {
    void publishAnamnesisSummaryCreated(AnamnesisSummaryCreatedEvent event);
    void publishAnamnesisSessionCompleted(AnamnesisSessionCompletedEvent event);
}

