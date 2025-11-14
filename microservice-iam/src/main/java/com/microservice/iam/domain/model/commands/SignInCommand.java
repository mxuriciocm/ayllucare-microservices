package com.microservice.iam.domain.model.commands;

/**
 * Command to sign in a user.
 * <p>
 *     This command is used to sign in a user in the system.
 *     It contains the username and password of the user.
 * </p>
 */
public record SignInCommand(String username, String password) {
}
