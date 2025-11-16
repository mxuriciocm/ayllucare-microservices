package com.microservice.triage.application.internal.commandservices;

import com.microservice.triage.application.events.TriageEventPublisher;
import com.microservice.triage.application.events.TriageResultCreatedEvent;
import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.domain.model.commands.CreateTriageResultCommand;
import com.microservice.triage.domain.services.TriageCommandService;
import com.microservice.triage.infrastructure.persistence.jpa.repositories.TriageResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of Triage Command Service.
 * Handles write operations for triage results.
 */
@Service
public class TriageCommandServiceImpl implements TriageCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TriageCommandServiceImpl.class);

    private final TriageResultRepository triageResultRepository;
    private final TriageEventPublisher eventPublisher;

    public TriageCommandServiceImpl(TriageResultRepository triageResultRepository,
                                   TriageEventPublisher eventPublisher) {
        this.triageResultRepository = triageResultRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<TriageResult> handle(CreateTriageResultCommand command) {
        logger.info("Creating triage result for userId: {}, sessionId: {}",
                   command.userId(), command.sessionId());

        try {
            // Check if triage already exists for this session
            Optional<TriageResult> existingTriage = triageResultRepository.findBySessionId(command.sessionId());
            if (existingTriage.isPresent()) {
                logger.warn("Triage result already exists for sessionId: {}", command.sessionId());
                return existingTriage;
            }

            // Create new triage result
            TriageResult triageResult = new TriageResult(
                command.userId(),
                command.sessionId(),
                command.priority(),
                command.riskFactors(),
                command.redFlagsDetected(),
                command.recommendations()
            );

            // Save to database
            TriageResult savedTriage = triageResultRepository.save(triageResult);
            logger.info("Triage result created successfully with ID: {}, Priority: {}",
                       savedTriage.getId(), savedTriage.getPriority());

            // Publish event to RabbitMQ
            publishTriageCreatedEvent(savedTriage);

            return Optional.of(savedTriage);

        } catch (Exception e) {
            logger.error("Error creating triage result for userId: {}", command.userId(), e);
            return Optional.empty();
        }
    }

    private void publishTriageCreatedEvent(TriageResult triageResult) {
        try {
            TriageResultCreatedEvent event = new TriageResultCreatedEvent(
                triageResult.getId(),
                triageResult.getUserId(),
                triageResult.getSessionId(),
                triageResult.getPriority(),
                triageResult.getRiskFactors(),
                triageResult.getRedFlagsDetected(),
                triageResult.getRecommendations()
            );

            eventPublisher.publishTriageResultCreated(event);
            logger.info("Published TriageResultCreatedEvent for triage ID: {}", triageResult.getId());

        } catch (Exception e) {
            logger.error("Error publishing TriageResultCreatedEvent for triage ID: {}",
                        triageResult.getId(), e);
            // Don't fail the transaction if event publishing fails
        }
    }
}

