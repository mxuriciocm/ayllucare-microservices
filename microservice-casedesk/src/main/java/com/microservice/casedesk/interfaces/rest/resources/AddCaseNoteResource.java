package com.microservice.casedesk.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record AddCaseNoteResource(
        @NotBlank(message = "Note cannot be empty")
        String note
) {}
