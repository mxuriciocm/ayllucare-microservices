package com.microservice.casedesk.domain.model.commands;

public record AddCaseNoteCommand(Long caseId, String note, Long performedByUserId) {
}
