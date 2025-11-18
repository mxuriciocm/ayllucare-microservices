package com.microservice.casedesk.application.internal.commandservices;

import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.commands.AddCaseNoteCommand;
import com.microservice.casedesk.domain.model.commands.AssignCaseCommand;
import com.microservice.casedesk.domain.model.commands.CreateCaseCommand;
import com.microservice.casedesk.domain.model.commands.UpdateCaseStatusCommand;
import com.microservice.casedesk.domain.model.events.CaseAssignedEvent;
import com.microservice.casedesk.domain.model.events.CaseStatusChangedEvent;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
import com.microservice.casedesk.domain.services.CaseCommandService;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseCommandServiceImpl implements CaseCommandService {

    private static final Logger logger = LoggerFactory.getLogger(CaseCommandServiceImpl.class);

    private final CaseRepository caseRepository;
    private final CaseEventPublisher caseEventPublisher;

    @Override
    @Transactional
    public Optional<Case> handle(CreateCaseCommand command) {
        try {
            // Convertir String a TriageLevel enum
            TriageLevel triageLevel;
            try {
                triageLevel = TriageLevel.valueOf(command.getTriageLevel().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("❌ Invalid triage level: {}", command.getTriageLevel());
                return Optional.empty();
            }

            // Crear caso usando el constructor correcto
            Case newCase = new Case(
                    command.getUserId(),
                    command.getTriageId(),
                    command.getAnamnesisSessionId(),
                    triageLevel,
                    null, // chiefComplaint (puedes agregarlo si lo tienes)
                    null, // mainRedFlags (puedes agregarlo si lo tienes)
                    command.getRecommendations()
            );

            Case savedCase = caseRepository.save(newCase);
            logger.info("✅ Case created successfully with ID: {} with triage level: {}",
                    savedCase.getId(), triageLevel);

            return Optional.of(savedCase);

        } catch (Exception e) {
            logger.error("❌ Error creating case: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }


    @Override
    @Transactional
    public Case handle(AssignCaseCommand command) {
        Case caseEntity = caseRepository.findById(command.caseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + command.caseId()));

        caseEntity.assignToDoctor(command.doctorId());
        Case savedCase = caseRepository.save(caseEntity);

        CaseAssignedEvent event = CaseAssignedEvent.create(
                savedCase.getId(),
                savedCase.getPatientId(),
                command.doctorId(),
                command.performedByUserId()
        );
        caseEventPublisher.publish(event);

        return savedCase;
    }

    @Override
    @Transactional
    public Case handle(UpdateCaseStatusCommand command) {
        Case caseEntity = caseRepository.findById(command.caseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + command.caseId()));

        CaseStatus previousStatus = caseEntity.getStatus();
        caseEntity.updateStatus(command.newStatus());
        Case savedCase = caseRepository.save(caseEntity);

        CaseStatusChangedEvent event = CaseStatusChangedEvent.create(
                savedCase.getId(),
                savedCase.getPatientId(),
                previousStatus,
                command.newStatus(),
                command.performedByUserId()
        );
        caseEventPublisher.publish(event);

        return savedCase;
    }

    @Override
    @Transactional
    public Case handle(AddCaseNoteCommand command) {
        Case caseEntity = caseRepository.findById(command.caseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + command.caseId()));

        caseEntity.addNote(command.note());
        return caseRepository.save(caseEntity);
    }
}
