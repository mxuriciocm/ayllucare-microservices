package com.microservice.profiles.domain.model.aggregates;

import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.domain.model.valueobjects.PersonName;
import com.microservice.profiles.domain.model.valueobjects.PhoneNumber;
import com.microservice.profiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient Profile aggregate root for AylluCare health platform.
 * Manages medical and personal information of patients.
 */
@Entity
@Table(name = "patient_profiles")
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    // User reference identifier (not a foreign key, references IAM service)
    @Column(unique = true, nullable = false)
    private Long userId;

    @Embedded
    private PersonName name;

    @Embedded
    private PhoneNumber phoneNumber;

    // Additional contact information
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Health information
    private LocalDate dateOfBirth;
    private String bloodType; // A+, A-, B+, B-, AB+, AB-, O+, O-
    private Double heightCm;
    private Double weightKg;

    // Medical history (stored as collections)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_allergies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_chronic_conditions", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "chronic_condition")
    private List<String> chronicConditions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_current_medications", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "medication")
    private List<String> currentMedications = new ArrayList<>();

    // Consent management (GDPR/HIPAA compliance)
    private Boolean consentForDataSharing;
    private Boolean consentForAIProcessing;
    private LocalDateTime consentSignedAt;

    /**
     * Constructor with userId, first name, last name, and phone number
     * @param userId User ID
     * @param firstName First name
     * @param lastName Last name
     * @param phoneNumber Phone number
     */
    public Profile(Long userId, String firstName, String lastName, String phoneNumber){
        this.userId = userId;
        this.name = new PersonName(firstName, lastName);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        // Initialize with no consent by default
        this.consentForDataSharing = false;
        this.consentForAIProcessing = false;
    }

    public Profile() {}

    public Profile(CreateProfileCommand command) {
        this.userId = command.userId();
        this.name = new PersonName(command.firstName(), command.lastName());
        this.phoneNumber = new PhoneNumber(command.phoneNumber());
        this.consentForDataSharing = false;
        this.consentForAIProcessing = false;
    }

    // Business methods for domain logic

    /**
     * Update patient allergies
     * @param allergies List of allergies
     */
    public void updateAllergies(List<String> allergies) {
        if (allergies != null) {
            this.allergies.clear();
            this.allergies.addAll(allergies);
        }
    }

    /**
     * Update chronic conditions
     * @param conditions List of chronic conditions
     */
    public void updateChronicConditions(List<String> conditions) {
        if (conditions != null) {
            this.chronicConditions.clear();
            this.chronicConditions.addAll(conditions);
        }
    }

    /**
     * Update current medications
     * @param medications List of current medications
     */
    public void updateCurrentMedications(List<String> medications) {
        if (medications != null) {
            this.currentMedications.clear();
            this.currentMedications.addAll(medications);
        }
    }

    /**
     * Sign consent for data sharing and AI processing
     * Required for GDPR/HIPAA compliance
     */
    public void signConsent() {
        this.consentForDataSharing = true;
        this.consentForAIProcessing = true;
        this.consentSignedAt = LocalDateTime.now();
    }

    /**
     * Revoke consent
     */
    public void revokeConsent() {
        this.consentForDataSharing = false;
        this.consentForAIProcessing = false;
        this.consentSignedAt = null;
    }

    /**
     * Check if patient has given consent for AI processing
     * @return true if consent is given
     */
    public boolean hasConsentForAI() {
        return Boolean.TRUE.equals(this.consentForAIProcessing);
    }

    /**
     * Calculate Body Mass Index (BMI)
     * BMI = weight(kg) / (height(m))^2
     * @return BMI value or null if height or weight is not set
     */
    public Double calculateBMI() {
        if (heightCm == null || weightKg == null || heightCm <= 0) {
            return null;
        }
        double heightInMeters = heightCm / 100.0;
        return weightKg / (heightInMeters * heightInMeters);
    }

    // Getters and setters

    public String getFullName() {
        return name.getFullName();
    }

    public void updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber.getPhoneNumber();
    }

    public Long getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public List<String> getChronicConditions() {
        return chronicConditions;
    }

    public List<String> getCurrentMedications() {
        return currentMedications;
    }

    public Boolean getConsentForDataSharing() {
        return consentForDataSharing;
    }

    public Boolean getConsentForAIProcessing() {
        return consentForAIProcessing;
    }

    public LocalDateTime getConsentSignedAt() {
        return consentSignedAt;
    }
}

