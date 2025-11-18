package com.microservice.casedesk.domain.model.aggregates;

import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.model.valueobjects.TriageLevel;
import com.microservice.casedesk.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Case extends AuditableAbstractAggregateRoot<Case> {

    @Column(nullable = false)
    private Long patientId;

    private Long triageId;

    @Column(nullable = false)
    private Long anamnesisSessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriageLevel triageLevel;

    @Column(columnDefinition = "TEXT")
    private String chiefComplaint;

    @ElementCollection
    @CollectionTable(name = "case_red_flags", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "note", columnDefinition = "TEXT")
    private List<String> mainRedFlags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    private Long assignedDoctorId;

    @ElementCollection
    @CollectionTable(name = "case_notes", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "note", columnDefinition = "TEXT")
    private List<String> notes = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String triageRecommendedAction;

    private LocalDateTime closedAt;

    public Case(Long patientId, Long triageId, Long anamnesisSessionId, TriageLevel triageLevel,
                String chiefComplaint, List<String> mainRedFlags, String triageRecommendedAction) {
        this.patientId = patientId;
        this.triageId = triageId;
        this.anamnesisSessionId = anamnesisSessionId;
        this.triageLevel = triageLevel;
        this.chiefComplaint = chiefComplaint;
        this.mainRedFlags = mainRedFlags != null ? new ArrayList<>(mainRedFlags) : new ArrayList<>();
        this.triageRecommendedAction = triageRecommendedAction;
        this.status = CaseStatus.OPEN;
    }

    public void assignToDoctor(Long doctorId) {
        if (this.status == CaseStatus.CLOSED) {
            throw new IllegalStateException("Cannot assign a closed case");
        }
        this.assignedDoctorId = doctorId;
        if (this.status == CaseStatus.OPEN) {
            this.status = CaseStatus.ASSIGNED;
        }
    }

    public void startHandling() {
        if (this.status != CaseStatus.ASSIGNED) {
            throw new IllegalStateException("Case must be assigned before starting handling");
        }
        this.status = CaseStatus.IN_PROGRESS;
    }

    public void markResolved() {
        if (this.status == CaseStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed case");
        }
        this.status = CaseStatus.RESOLVED;
    }

    public void close() {
        this.status = CaseStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public void updateStatus(CaseStatus newStatus) {
        if (this.status == CaseStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed case");
        }

        switch (newStatus) {
            case IN_PROGRESS -> {
                if (this.status != CaseStatus.ASSIGNED) {
                    throw new IllegalStateException("Case must be assigned before moving to IN_PROGRESS");
                }
            }
            case CLOSED -> {
                this.closedAt = LocalDateTime.now();
            }
        }

        this.status = newStatus;
    }

    public void addNote(String note) {
        if (note != null && !note.isBlank()) {
            this.notes.add(note);
        }
    }

    // Setters para CreateCaseCommand
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setTriageId(Long triageId) {
        this.triageId = triageId;
    }

    public void setAnamnesisSessionId(Long anamnesisSessionId) {
        this.anamnesisSessionId = anamnesisSessionId;
    }

    public void setTriageLevel(TriageLevel triageLevel) {
        this.triageLevel = triageLevel;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

}
