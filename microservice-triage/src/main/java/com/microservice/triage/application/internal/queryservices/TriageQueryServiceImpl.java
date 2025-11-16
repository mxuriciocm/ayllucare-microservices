package com.microservice.triage.application.internal.queryservices;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.domain.model.queries.GetAllTriagesQuery;
import com.microservice.triage.domain.model.queries.GetTriageByIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageBySessionIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageByUserIdQuery;
import com.microservice.triage.domain.services.TriageQueryService;
import com.microservice.triage.infrastructure.persistence.jpa.repositories.TriageResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of Triage Query Service.
 * Handles read operations for triage results.
 */
@Service
@Transactional(readOnly = true)
public class TriageQueryServiceImpl implements TriageQueryService {

    private static final Logger logger = LoggerFactory.getLogger(TriageQueryServiceImpl.class);

    private final TriageResultRepository triageResultRepository;

    public TriageQueryServiceImpl(TriageResultRepository triageResultRepository) {
        this.triageResultRepository = triageResultRepository;
    }

    @Override
    public Optional<TriageResult> handle(GetTriageByIdQuery query) {
        logger.debug("Fetching triage result with ID: {}", query.triageId());
        return triageResultRepository.findById(query.triageId());
    }

    @Override
    public List<TriageResult> handle(GetTriageByUserIdQuery query) {
        logger.debug("Fetching triage results for userId: {}", query.userId());
        return triageResultRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<TriageResult> handle(GetTriageBySessionIdQuery query) {
        logger.debug("Fetching triage result for sessionId: {}", query.sessionId());
        return triageResultRepository.findBySessionId(query.sessionId());
    }

    @Override
    public List<TriageResult> handle(GetAllTriagesQuery query) {
        logger.debug("Fetching all triage results");
        return triageResultRepository.findAll();
    }
}
