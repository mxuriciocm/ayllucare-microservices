package com.microservice.casedesk.domain.services;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.queries.GetAllOpenCasesQuery;
import com.microservice.casedesk.domain.model.queries.GetCaseByIdQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByDoctorAndStatusQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByPatientQuery;

import java.util.List;
import java.util.Optional;

public interface CaseQueryService {
    Optional<Case> handle(GetCaseByIdQuery query);
    List<Case> handle(GetCasesByPatientQuery query);
    List<Case> handle(GetCasesByDoctorAndStatusQuery query);
    List<Case> handle(GetAllOpenCasesQuery query);
}
