package com.microservice.casedesk.domain.services;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.commands.AddCaseNoteCommand;
import com.microservice.casedesk.domain.model.commands.AssignCaseCommand;
import com.microservice.casedesk.domain.model.commands.CreateCaseCommand;
import com.microservice.casedesk.domain.model.commands.UpdateCaseStatusCommand;

import java.util.Optional;

public interface CaseCommandService {
    Case handle(AssignCaseCommand command);
    Case handle(UpdateCaseStatusCommand command);
    Case handle(AddCaseNoteCommand command);
    Optional<Case> handle(CreateCaseCommand command);
}
