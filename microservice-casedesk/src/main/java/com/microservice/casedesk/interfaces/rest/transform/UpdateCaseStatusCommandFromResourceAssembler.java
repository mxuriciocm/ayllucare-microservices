package com.microservice.casedesk.interfaces.rest.transform;

import com.microservice.casedesk.domain.model.commands.UpdateCaseStatusCommand;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.interfaces.rest.resources.UpdateCaseStatusResource;

public class UpdateCaseStatusCommandFromResourceAssembler {

    public static UpdateCaseStatusCommand toCommandFromResource(Long caseId, UpdateCaseStatusResource resource, Long performedByUserId) {
        CaseStatus status = CaseStatus.valueOf(resource.status().toUpperCase());
        return new UpdateCaseStatusCommand(caseId, status, performedByUserId);
    }
}
