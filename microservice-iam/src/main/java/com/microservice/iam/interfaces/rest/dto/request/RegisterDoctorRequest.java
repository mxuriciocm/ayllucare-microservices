package com.microservice.iam.interfaces.rest.dto.request;

public record RegisterDoctorRequest(
    String firstName,
    String lastName,
    String email,
    String password,
    String phoneNumber,
    Boolean requiresVerification
) {}

