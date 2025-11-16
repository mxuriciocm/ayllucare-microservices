package com.microservice.casedesk.domain.model.commands;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;

public record UpdateCaseStatusCommand(Long caseId, CaseStatus newStatus, Long performedByUserId) {
}
