package com.microservice.anamnesis.interfaces.rest.resources;

import com.microservice.anamnesis.domain.model.valueobjects.ConversationMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * REST resource with full session details including messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnamnesisSessionDetailResource {
    private AnamnesisSessionResource session;
    private List<ConversationMessage> messages;
}

