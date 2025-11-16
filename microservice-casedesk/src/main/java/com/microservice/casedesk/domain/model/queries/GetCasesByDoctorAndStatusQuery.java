package com.microservice.casedesk.domain.model.queries;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;

public record GetCasesByDoctorAndStatusQuery(Long doctorId, CaseStatus status) {
}
