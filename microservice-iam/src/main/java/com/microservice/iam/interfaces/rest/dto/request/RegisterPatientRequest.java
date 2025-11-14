package com.microservice.iam.interfaces.rest.dto.request;

public record RegisterPatientRequest(
    String firstName,
    String lastName,
    String email,
    String password,
    String phoneNumber,
    String preferredLanguage
) {}

