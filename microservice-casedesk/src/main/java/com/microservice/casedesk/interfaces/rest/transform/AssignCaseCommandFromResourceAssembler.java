package com.microservice.casedesk.interfaces.rest.transform;

import com.microservice.casedesk.domain.model.commands.AssignCaseCommand;
import com.microservice.casedesk.interfaces.rest.resources.AssignCaseResource;

public class AssignCaseCommandFromResourceAssembler {

    public static AssignCaseCommand toCommandFromResource(Long caseId, AssignCaseResource resource, Long performedByUserId) {
        return new AssignCaseCommand(caseId, resource.doctorId(), performedByUserId);
    }
}
