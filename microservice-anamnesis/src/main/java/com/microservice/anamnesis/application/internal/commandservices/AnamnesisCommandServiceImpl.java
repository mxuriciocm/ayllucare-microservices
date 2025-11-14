package com.microservice.anamnesis.application.internal.commandservices;

import com.microservice.anamnesis.application.clients.LlmClient;
import com.microservice.anamnesis.application.clients.ProfileClient;
import com.microservice.anamnesis.application.dto.ProfileSnapshot;
import com.microservice.anamnesis.application.events.AnamnesisEventPublisher;
import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.commands.AddMessageToSessionCommand;
import com.microservice.anamnesis.domain.model.commands.CancelAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.commands.CompleteAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.commands.StartAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.events.AnamnesisSessionCompletedEvent;
import com.microservice.anamnesis.domain.model.events.AnamnesisSummaryCreatedEvent;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import com.microservice.anamnesis.domain.services.AnamnesisCommandService;
import com.microservice.anamnesis.infrastructure.persistence.jpa.repositories.AnamnesisSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of AnamnesisCommandService.
 * Orchestrates business logic for anamnesis session commands,
 * including LLM integration, profile checks, and event publishing.
 */
@Service
public class AnamnesisCommandServiceImpl implements AnamnesisCommandService {

    private static final Logger logger = LoggerFactory.getLogger(AnamnesisCommandServiceImpl.class);

    private final AnamnesisSessionRepository sessionRepository;
    private final LlmClient llmClient;
    private final ProfileClient profileClient;
    private final AnamnesisEventPublisher eventPublisher;

    public AnamnesisCommandServiceImpl(
            AnamnesisSessionRepository sessionRepository,
            LlmClient llmClient,
            ProfileClient profileClient,
            AnamnesisEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.llmClient = llmClient;
        this.profileClient = profileClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<AnamnesisSession> handle(StartAnamnesisSessionCommand command) {
        logger.info("Starting new anamnesis session for userId: {}", command.userId());

        try {
            if (!profileClient.hasConsentForAIProcessing(command.userId())) {
                logger.warn("User {} has not given consent for AI processing", command.userId());
                throw new IllegalStateException("El usuario no ha dado consentimiento para procesamiento con IA");
            }

            var session = new AnamnesisSession(command.userId(), command.initialReason());
            var savedSession = sessionRepository.save(session);

            logger.info("Anamnesis session created with ID: {}", savedSession.getId());
            return Optional.of(savedSession);

        } catch (Exception e) {
            logger.error("Error starting anamnesis session for userId: {}", command.userId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<AnamnesisSession> handle(AddMessageToSessionCommand command) {
        logger.info("Adding message to session: {}", command.sessionId());

        try {
            var session = sessionRepository.findById(command.sessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + command.sessionId()));

            if (!session.getUserId().equals(command.userId())) {
                throw new IllegalStateException("Usuario no autorizado para esta sesión");
            }

            session.addPatientMessage(command.content());

            ProfileSnapshot profile = profileClient.getProfileByUserId(command.userId()).orElse(null);

            if (llmClient.isAvailable()) {
                try {
                    String assistantResponse = llmClient.generateResponse(session, profile);
                    session.addAssistantMessage(assistantResponse);
                } catch (Exception e) {
                    logger.error("Error calling LLM service", e);
                    session.addSystemMessage("Error al generar respuesta del asistente. Por favor, intente nuevamente.");
                }
            } else {
                logger.warn("LLM service not available");
                session.addSystemMessage("Servicio de IA temporalmente no disponible.");
            }

            var updatedSession = sessionRepository.save(session);
            return Optional.of(updatedSession);

        } catch (Exception e) {
            logger.error("Error adding message to session: {}", command.sessionId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<AnamnesisSession> handle(CompleteAnamnesisSessionCommand command) {
        logger.info("Completing anamnesis session: {}", command.sessionId());

        try {
            var session = sessionRepository.findById(command.sessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + command.sessionId()));

            if (!session.getUserId().equals(command.userId())) {
                throw new IllegalStateException("Usuario no autorizado para esta sesión");
            }

            if (session.isCompleted()) {
                logger.warn("Session {} is already completed", command.sessionId());
                return Optional.of(session);
            }

            ProfileSnapshot profile = profileClient.getProfileByUserId(command.userId()).orElse(null);

            AnamnesisSummary summary;
            if (llmClient.isAvailable()) {
                summary = llmClient.generateSummary(session, profile);
            } else {
                logger.warn("LLM service not available, creating empty summary");
                summary = AnamnesisSummary.empty();
            }

            session.completeWithSummary(summary);
            var completedSession = sessionRepository.save(session);

            publishCompletionEvents(completedSession);

            logger.info("Anamnesis session {} completed successfully", command.sessionId());
            return Optional.of(completedSession);

        } catch (Exception e) {
            logger.error("Error completing session: {}", command.sessionId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<AnamnesisSession> handle(CancelAnamnesisSessionCommand command) {
        logger.info("Cancelling anamnesis session: {}", command.sessionId());

        try {
            var session = sessionRepository.findById(command.sessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + command.sessionId()));

            if (!session.getUserId().equals(command.userId())) {
                throw new IllegalStateException("Usuario no autorizado para esta sesión");
            }

            session.cancelSession(command.reason());
            var cancelledSession = sessionRepository.save(session);

            logger.info("Anamnesis session {} cancelled", command.sessionId());
            return Optional.of(cancelledSession);

        } catch (Exception e) {
            logger.error("Error cancelling session: {}", command.sessionId(), e);
            throw e;
        }
    }

    private void publishCompletionEvents(AnamnesisSession session) {
        try {
            var completedEvent = new AnamnesisSessionCompletedEvent(
                    session.getId(),
                    session.getUserId(),
                    session.getStatus()
            );
            eventPublisher.publishAnamnesisSessionCompleted(completedEvent);

            if (session.hasSummary()) {
                var summaryEvent = new AnamnesisSummaryCreatedEvent(
                        session.getId(),
                        session.getUserId(),
                        session.getSummary()
                );
                eventPublisher.publishAnamnesisSummaryCreated(summaryEvent);
            }
        } catch (Exception e) {
            logger.error("Error publishing completion events for session {}", session.getId(), e);
        }
    }
}

