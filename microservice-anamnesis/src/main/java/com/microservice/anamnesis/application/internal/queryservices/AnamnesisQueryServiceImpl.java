package com.microservice.anamnesis.application.internal.queryservices;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.queries.GetAllSessionsQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionByIdQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionsByUserIdAndStatusQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionsByUserIdQuery;
import com.microservice.anamnesis.domain.services.AnamnesisQueryService;
import com.microservice.anamnesis.infrastructure.persistence.jpa.repositories.AnamnesisSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnamnesisQueryServiceImpl implements AnamnesisQueryService {

    private static final Logger logger = LoggerFactory.getLogger(AnamnesisQueryServiceImpl.class);
    private final AnamnesisSessionRepository sessionRepository;

    public AnamnesisQueryServiceImpl(AnamnesisSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Optional<AnamnesisSession> handle(GetSessionByIdQuery query) {
        logger.debug("Fetching session by ID: {}", query.sessionId());
        return sessionRepository.findById(query.sessionId());
    }

    @Override
    public List<AnamnesisSession> handle(GetSessionsByUserIdQuery query) {
        logger.debug("Fetching sessions for userId: {}", query.userId());
        return sessionRepository.findByUserId(query.userId());
    }

    @Override
    public List<AnamnesisSession> handle(GetSessionsByUserIdAndStatusQuery query) {
        logger.debug("Fetching sessions for userId: {} with status: {}", query.userId(), query.status());
        return sessionRepository.findByUserIdAndStatus(query.userId(), query.status());
    }

    @Override
    public List<AnamnesisSession> handle(GetAllSessionsQuery query) {
        logger.debug("Fetching all sessions");
        return sessionRepository.findAll();
    }
}

