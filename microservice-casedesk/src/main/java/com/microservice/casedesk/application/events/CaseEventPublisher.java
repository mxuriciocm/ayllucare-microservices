package com.microservice.casedesk.application.events;

import com.microservice.casedesk.domain.model.events.CaseAssignedEvent;
import com.microservice.casedesk.domain.model.events.CaseCreatedEvent;
import com.microservice.casedesk.domain.model.events.CaseStatusChangedEvent;

public interface CaseEventPublisher {
    void publish(CaseCreatedEvent event);
    void publish(CaseAssignedEvent event);
    void publish(CaseStatusChangedEvent event);
}
