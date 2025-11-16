package com.microservice.triage.domain.model.aggregates;

import com.microservice.triage.domain.model.valueobjects.PriorityLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Triage Result Aggregate Root.
 * Represents the result of a medical triage assessment based on anamnesis data.
 *
 * Domain invariants:
 * - A triage result must have a priority level
 * - Must be associated with a user and an anamnesis session
 * - Once created, the priority can be updated if new information arrives
 * - Risk factors and red flags must be preserved for audit
 */
@Entity
@Table(name = "triage_results")
@Getter
@NoArgsConstructor
public class TriageResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriorityLevel priority;

    @ElementCollection
    @CollectionTable(name = "triage_risk_factors", joinColumns = @JoinColumn(name = "triage_result_id"))
    @Column(name = "risk_factor", length = 500)
    private List<String> riskFactors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "triage_red_flags", joinColumns = @JoinColumn(name = "triage_result_id"))
    @Column(name = "red_flag", length = 500)
    private List<String> redFlagsDetected = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Creates a new triage result.
     *
     * @param userId The patient's user ID
     * @param sessionId The anamnesis session ID
     * @param priority The calculated priority level
     * @param riskFactors List of identified risk factors
     * @param redFlags List of detected red flags
     * @param recommendations Medical recommendations
     */
    public TriageResult(Long userId, Long sessionId, PriorityLevel priority,
                       List<String> riskFactors, List<String> redFlags,
                       String recommendations) {
        validateUserId(userId);
        validateSessionId(sessionId);
        validatePriority(priority);

        this.userId = userId;
        this.sessionId = sessionId;
        this.priority = priority;
        this.riskFactors = riskFactors != null ? new ArrayList<>(riskFactors) : new ArrayList<>();
        this.redFlagsDetected = redFlags != null ? new ArrayList<>(redFlags) : new ArrayList<>();
        this.recommendations = recommendations;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the priority level if reassessment is needed.
     *
     * @param newPriority The new priority level
     */
    public void updatePriority(PriorityLevel newPriority) {
        validatePriority(newPriority);
        this.priority = newPriority;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a new risk factor to the assessment.
     *
     * @param riskFactor The risk factor to add
     */
    public void addRiskFactor(String riskFactor) {
        if (riskFactor != null && !riskFactor.isBlank()) {
            if (!this.riskFactors.contains(riskFactor)) {
                this.riskFactors.add(riskFactor);
                this.updatedAt = LocalDateTime.now();
            }
        }
    }

    /**
     * Adds a new red flag to the assessment.
     *
     * @param redFlag The red flag to add
     */
    public void addRedFlag(String redFlag) {
        if (redFlag != null && !redFlag.isBlank()) {
            if (!this.redFlagsDetected.contains(redFlag)) {
                this.redFlagsDetected.add(redFlag);
                this.updatedAt = LocalDateTime.now();
            }
        }
    }

    /**
     * Updates the recommendations.
     *
     * @param recommendations New recommendations
     */
    public void updateRecommendations(String recommendations) {
        this.recommendations = recommendations;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if this is an emergency case.
     *
     * @return true if priority is EMERGENCY
     */
    public boolean isEmergency() {
        return PriorityLevel.EMERGENCY.equals(this.priority);
    }

    /**
     * Checks if this case has red flags.
     *
     * @return true if red flags are detected
     */
    public boolean hasRedFlags() {
        return this.redFlagsDetected != null && !this.redFlagsDetected.isEmpty();
    }

    /**
     * Gets the count of risk factors.
     *
     * @return number of risk factors
     */
    public int getRiskFactorCount() {
        return this.riskFactors != null ? this.riskFactors.size() : 0;
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }

    private void validateSessionId(Long sessionId) {
        if (sessionId == null || sessionId <= 0) {
            throw new IllegalArgumentException("Session ID must be a positive number");
        }
    }

    private void validatePriority(PriorityLevel priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

