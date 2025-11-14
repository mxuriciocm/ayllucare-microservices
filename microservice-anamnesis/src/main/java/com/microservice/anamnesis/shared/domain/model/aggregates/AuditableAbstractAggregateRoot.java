package com.microservice.anamnesis.shared.domain.model.aggregates;

import com.microservice.anamnesis.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * Base aggregate root with auditing support.
 * All aggregate roots should extend this class to inherit ID and audit fields.
 *
 * @param <T> the type of the aggregate root
 */
@Getter
@MappedSuperclass
public class AuditableAbstractAggregateRoot<T> extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

