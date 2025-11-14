package com.microservice.profiles.application.events;

public record UserCreatedEvent(Long userId, String email) {}
