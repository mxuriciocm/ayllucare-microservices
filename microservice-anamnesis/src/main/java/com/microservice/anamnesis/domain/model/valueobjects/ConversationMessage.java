package com.microservice.anamnesis.domain.model.valueobjects;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    public static ConversationMessage fromPatient(String content) {
        return new ConversationMessage(SenderType.PATIENT, content, Instant.now());
    }

    public static ConversationMessage fromAssistant(String content) {
        return new ConversationMessage(SenderType.ASSISTANT, content, Instant.now());
    }

    public static ConversationMessage fromSystem(String content) {
        return new ConversationMessage(SenderType.SYSTEM, content, Instant.now());
    }
}

