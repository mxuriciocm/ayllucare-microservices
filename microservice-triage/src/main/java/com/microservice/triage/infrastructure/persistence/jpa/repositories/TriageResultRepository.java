package com.microservice.triage.infrastructure.persistence.jpa.repositories;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TriageResult aggregate.
 */
@Repository
public interface TriageResultRepository extends JpaRepository<TriageResult, Long> {

    /**
     * Finds all triage results for a specific user.
     *
     * @param userId The user ID
     * @return List of triage results
     */
    List<TriageResult> findByUserId(Long userId);

    /**
     * Finds a triage result by anamnesis session ID.
     *
     * @param sessionId The session ID
     * @return The triage result if found
     */
    Optional<TriageResult> findBySessionId(Long sessionId);

    /**
     * Checks if a triage result exists for a session.
     *
     * @param sessionId The session ID
     * @return true if exists
     */
    boolean existsBySessionId(Long sessionId);
}

