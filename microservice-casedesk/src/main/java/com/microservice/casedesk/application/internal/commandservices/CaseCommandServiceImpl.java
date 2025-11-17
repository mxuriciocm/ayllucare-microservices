package com.microservice.casedesk.application.internal.commandservices;

import com.microservice.casedesk.application.events.CaseEventPublisher;
import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.commands.AddCaseNoteCommand;
import com.microservice.casedesk.domain.model.commands.AssignCaseCommand;
import com.microservice.casedesk.domain.model.commands.UpdateCaseStatusCommand;
import com.microservice.casedesk.domain.model.events.CaseAssignedEvent;
import com.microservice.casedesk.domain.model.events.CaseStatusChangedEvent;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.services.CaseCommandService;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseCommandServiceImpl implements CaseCommandService {

    private final CaseRepository caseRepository;
    private final CaseEventPublisher caseEventPublisher;

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
