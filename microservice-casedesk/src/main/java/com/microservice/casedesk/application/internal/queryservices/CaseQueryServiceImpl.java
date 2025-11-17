package com.microservice.casedesk.application.internal.queryservices;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.queries.GetAllOpenCasesQuery;
import com.microservice.casedesk.domain.model.queries.GetCaseByIdQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByDoctorAndStatusQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByPatientQuery;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.services.CaseQueryService;
import com.microservice.casedesk.infrastructure.persistence.jpa.repositories.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseQueryServiceImpl implements CaseQueryService {

    private final CaseRepository caseRepository;

    @Override
    public Optional<Case> handle(GetCaseByIdQuery query) {
        return caseRepository.findById(query.caseId());
    }

    @Override
    public List<Case> handle(GetCasesByPatientQuery query) {
        return caseRepository.findByPatientId(query.patientId());
    }

    @Override
    public List<Case> handle(GetCasesByDoctorAndStatusQuery query) {
        if (query.status() != null) {
            return caseRepository.findByAssignedDoctorIdAndStatus(query.doctorId(), query.status());
        }
        return caseRepository.findByAssignedDoctorId(query.doctorId());
    }

    @Override
    public List<Case> handle(GetAllOpenCasesQuery query) {
        return caseRepository.findByStatus(CaseStatus.OPEN);
    }
}
