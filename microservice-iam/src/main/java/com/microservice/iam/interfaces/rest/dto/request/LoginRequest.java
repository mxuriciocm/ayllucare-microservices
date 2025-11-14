package com.microservice.iam.interfaces.rest.dto.request;

public record LoginRequest(
    String email,
    String password
) {}

