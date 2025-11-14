package com.microservice.iam.interfaces.rest.dto.response;

import com.microservice.iam.domain.model.aggregates.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserResponse - DTO for user information in AylluCare/B4U.
 * <p>
 *     Used to return user data without sensitive information like passwords.
 * </p>
 *
 * @param userId the user's unique identifier
 * @param email the user's email address
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param roles the user's roles
 * @param status the user's account status
 * @param phoneNumber the user's phone number
 * @param preferredLanguage the user's preferred language
 */
public record UserResponse(
    Long userId,
    String email,
    String firstName,
    String lastName,
    Set<String> roles,
    String status,
    String phoneNumber,
    String preferredLanguage
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()),
            user.getStatus().name(),
            user.getPhoneNumber(),
            user.getPreferredLanguage()
        );
    }
}

