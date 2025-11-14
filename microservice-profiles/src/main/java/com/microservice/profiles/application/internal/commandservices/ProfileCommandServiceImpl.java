package com.microservice.profiles.application.internal.commandservices;

import com.microservice.profiles.domain.model.aggregates.Profile;
import com.microservice.profiles.domain.model.commands.CreateProfileCommand;
import com.microservice.profiles.domain.model.commands.SignConsentCommand;
import com.microservice.profiles.domain.model.commands.UpdateProfileCommand;
import com.microservice.profiles.domain.services.ProfileCommandService;
import com.microservice.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository userProfileRepository;

    public ProfileCommandServiceImpl(ProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        var userProfile = new Profile(command);
        var savedProfile = userProfileRepository.save(userProfile);
        return Optional.of(savedProfile);
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        Optional<Profile> existingProfile = userProfileRepository.findByUserId(command.profileId());
        if (existingProfile.isEmpty()) {
            return Optional.empty();
        }

        Profile profile = existingProfile.get();

        // Update name
        if (command.firstName() != null || command.lastName() != null) {
            String firstName = command.firstName() != null ? command.firstName() : profile.getFullName().split(" ")[0];
            String lastName = command.lastName() != null ? command.lastName() :
                              (profile.getFullName().split(" ").length > 1 ? profile.getFullName().split(" ")[1] : "");
            profile.updateName(firstName, lastName);
        }

        // Update contact information
        if (command.phoneNumber() != null) {
            profile.updatePhoneNumber(command.phoneNumber());
        }
        if (command.address() != null) {
            profile.setAddress(command.address());
        }
        if (command.emergencyContactName() != null) {
            profile.setEmergencyContactName(command.emergencyContactName());
        }
        if (command.emergencyContactPhone() != null) {
            profile.setEmergencyContactPhone(command.emergencyContactPhone());
        }

        // Update health information
        if (command.dateOfBirth() != null) {
            profile.setDateOfBirth(command.dateOfBirth());
        }
        if (command.bloodType() != null) {
            profile.setBloodType(command.bloodType());
        }
        if (command.heightCm() != null) {
            profile.setHeightCm(command.heightCm());
        }
        if (command.weightKg() != null) {
            profile.setWeightKg(command.weightKg());
        }

        // Update medical history
        if (command.allergies() != null) {
            profile.updateAllergies(command.allergies());
        }
        if (command.chronicConditions() != null) {
            profile.updateChronicConditions(command.chronicConditions());
        }
        if (command.currentMedications() != null) {
            profile.updateCurrentMedications(command.currentMedications());
        }

        var updatedProfile = userProfileRepository.save(profile);
        return Optional.of(updatedProfile);
    }

    @Override
    public Optional<Profile> handle(SignConsentCommand command) {
        Optional<Profile> existingProfile = userProfileRepository.findByUserId(command.userId());
        if (existingProfile.isEmpty()) {
            return Optional.empty();
        }

        Profile profile = existingProfile.get();
        profile.signConsent();

        var updatedProfile = userProfileRepository.save(profile);
        return Optional.of(updatedProfile);
    }
}
