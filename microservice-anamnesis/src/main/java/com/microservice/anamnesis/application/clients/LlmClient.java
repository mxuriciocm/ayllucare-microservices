package com.microservice.anamnesis.application.clients;

import com.microservice.anamnesis.application.dto.ProfileSnapshot;
import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;

public interface LlmClient {
    String generateResponse(AnamnesisSession session, ProfileSnapshot profile);
    AnamnesisSummary generateSummary(AnamnesisSession session, ProfileSnapshot profile);
    boolean isAvailable();
}

