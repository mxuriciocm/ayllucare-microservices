package com.microservice.casedesk.interfaces.rest.transform;

import com.microservice.casedesk.domain.model.commands.AddCaseNoteCommand;
import com.microservice.casedesk.interfaces.rest.resources.AddCaseNoteResource;

public class AddCaseNoteCommandFromResourceAssembler {

    public static AddCaseNoteCommand toCommandFromResource(Long caseId, AddCaseNoteResource resource, Long performedByUserId) {
        return new AddCaseNoteCommand(caseId, resource.note(), performedByUserId);
    }
}
