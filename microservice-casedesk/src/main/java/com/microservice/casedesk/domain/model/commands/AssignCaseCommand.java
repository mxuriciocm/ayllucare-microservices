package com.microservice.casedesk.domain.model.commands;

public record AssignCaseCommand(Long caseId, Long doctorId, Long performedByUserId) {
}
