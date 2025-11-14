package com.microservice.anamnesis.domain.model.aggregates;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisStatus;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import com.microservice.anamnesis.domain.model.valueobjects.ConversationMessage;
import com.microservice.anamnesis.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * AnamnesisSession aggregate root for AylluCare/B4U platform.
 * <p>
 * Represents a conversational anamnesis session between a patient and an AI assistant.
 * Manages the conversation history, status, and final summary.
 * </p>
 * <p>
 * Domain invariants:
 * - A session belongs to exactly one userId
 * - Once status is COMPLETED, summary cannot be null
 * - Messages can only be added when status is CREATED or IN_PROGRESS
 * - Status transitions: CREATED -> IN_PROGRESS -> COMPLETED/CANCELLED
 * </p>
 */
@Getter
@Entity
@Table(name = "anamnesis_sessions")
public class AnamnesisSession extends AuditableAbstractAggregateRoot<AnamnesisSession> {
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnamnesisStatus status;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "conversation_messages", 
                     joinColumns = @JoinColumn(name = "session_id"))
    @OrderColumn(name = "message_order")
    private List<ConversationMessage> messages = new ArrayList<>();
    @Embedded
    private AnamnesisSummary summary;
    @Column(columnDefinition = "TEXT")
    private String initialReason;
    public AnamnesisSession() {
        this.messages = new ArrayList<>();
        this.status = AnamnesisStatus.CREATED;
    }
    public AnamnesisSession(Long userId, String initialReason) {
        this();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.userId = userId;
        this.initialReason = initialReason;
        this.messages.add(ConversationMessage.fromSystem(
            "Sesión de anamnesis iniciada. Por favor, cuénteme el motivo de su consulta."
        ));
    }
    public void startSession() {
        if (this.status != AnamnesisStatus.CREATED) {
            throw new IllegalStateException("Session can only be started from CREATED status");
        }
        this.status = AnamnesisStatus.IN_PROGRESS;
    }
    public void addPatientMessage(String content) {
        validateCanAddMessage();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        this.messages.add(ConversationMessage.fromPatient(content));
        if (this.status == AnamnesisStatus.CREATED) {
            this.status = AnamnesisStatus.IN_PROGRESS;
        }
    }
    public void addAssistantMessage(String content) {
        validateCanAddMessage();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Assistant message content cannot be empty");
        }
        this.messages.add(ConversationMessage.fromAssistant(content));
    }
    public void addSystemMessage(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("System message content cannot be empty");
        }
        this.messages.add(ConversationMessage.fromSystem(content));
    }
    public void markInProgress() {
        if (this.status == AnamnesisStatus.COMPLETED || this.status == AnamnesisStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a completed or cancelled session");
        }
        this.status = AnamnesisStatus.IN_PROGRESS;
    }
    public void completeWithSummary(AnamnesisSummary summary) {
        if (this.status == AnamnesisStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled session");
        }
        if (this.status == AnamnesisStatus.COMPLETED) {
            throw new IllegalStateException("Session is already completed");
        }
        if (summary == null) {
            throw new IllegalArgumentException("Summary cannot be null when completing session");
        }
        this.summary = summary;
        this.status = AnamnesisStatus.COMPLETED;
        this.addSystemMessage("Anamnesis completada. Resumen generado exitosamente.");
    }
    public void cancelSession(String reason) {
        if (this.status == AnamnesisStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed session");
        }
        if (this.status == AnamnesisStatus.CANCELLED) {
            throw new IllegalStateException("Session is already cancelled");
        }
        this.status = AnamnesisStatus.CANCELLED;
        String message = "Sesión cancelada" + (reason != null && !reason.isBlank() ? ": " + reason : "");
        this.addSystemMessage(message);
    }
    public List<ConversationMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    public boolean isActive() {
        return status == AnamnesisStatus.CREATED || status == AnamnesisStatus.IN_PROGRESS;
    }
    public boolean isCompleted() {
        return status == AnamnesisStatus.COMPLETED;
    }
    public boolean hasSummary() {
        return summary != null && !summary.isEmpty();
    }
    public String getConversationHistory() {
        StringBuilder history = new StringBuilder();
        for (ConversationMessage message : messages) {
            String prefix = switch (message.getSenderType()) {
                case PATIENT -> "Paciente: ";
                case ASSISTANT -> "Asistente: ";
                case SYSTEM -> "Sistema: ";
            };
            history.append(prefix).append(message.getContent()).append("\n");
        }
        return history.toString();
    }
    private void validateCanAddMessage() {
        if (this.status == AnamnesisStatus.COMPLETED) {
            throw new IllegalStateException("Cannot add messages to a completed session");
        }
        if (this.status == AnamnesisStatus.CANCELLED) {
            throw new IllegalStateException("Cannot add messages to a cancelled session");
        }
    }
}
