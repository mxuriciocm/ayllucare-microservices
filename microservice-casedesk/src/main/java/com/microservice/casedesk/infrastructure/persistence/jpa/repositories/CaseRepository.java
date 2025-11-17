package com.microservice.casedesk.infrastructure.persistence.jpa.repositories;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByPatientId(Long patientId);
    List<Case> findByAssignedDoctorId(Long doctorId);
    List<Case> findByAssignedDoctorIdAndStatus(Long doctorId, CaseStatus status);
    List<Case> findByStatus(CaseStatus status);
}
