package com.microservice.casedesk.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaseCommand {
    private Long userId;
    private Long triageId;
    private Long anamnesisSessionId;
    private String triageLevel;
    private String recommendations;
}
