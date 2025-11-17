package com.microservice.casedesk.application.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSummaryCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private Long sessionId;
    private Long userId;
    private AnamnesisSummaryPayload summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnamnesisSummaryPayload {
        private String chiefComplaint;
        private String historyOfPresentIllness;
        private List<String> medications;
        private List<String> allergies;
        private List<String> redFlags;
        private String additionalNotes;
    }
}
