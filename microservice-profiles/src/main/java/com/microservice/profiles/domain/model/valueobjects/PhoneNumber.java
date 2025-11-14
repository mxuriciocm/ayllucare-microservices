package com.microservice.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(String phoneNumber) {

    public PhoneNumber {
        // Convert null or blank to empty string
        phoneNumber = (phoneNumber == null) ? "" : phoneNumber.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
