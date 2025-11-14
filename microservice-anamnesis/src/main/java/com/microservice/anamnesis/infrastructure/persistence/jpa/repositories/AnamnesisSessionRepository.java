package com.microservice.anamnesis.infrastructure.persistence.jpa.repositories;

import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * JPA Repository for AnamnesisSession aggregate.
 */
@Repository
public interface AnamnesisSessionRepository extends JpaRepository<AnamnesisSession, Long> {

    List<AnamnesisSession> findByUserId(Long userId);

    List<AnamnesisSession> findByUserIdAndStatus(Long userId, AnamnesisStatus status);

    List<AnamnesisSession> findByStatus(AnamnesisStatus status);

    boolean existsByUserIdAndStatus(Long userId, AnamnesisStatus status);
}

