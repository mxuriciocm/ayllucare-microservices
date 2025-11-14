package com.microservice.iam.interfaces.rest.controllers;

import com.microservice.iam.domain.services.UserCommandService;
import com.microservice.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.microservice.iam.interfaces.rest.resources.SignInResource;
import com.microservice.iam.interfaces.rest.resources.SignUpResource;
import com.microservice.iam.interfaces.rest.resources.UserResource;
import com.microservice.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.microservice.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.microservice.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.microservice.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthenticationController for AylluCare/B4U platform.
 * Simplified version following original pattern with Assemblers.
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints for AylluCare/B4U")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Sign up a new user.
     *
     * @param resource the sign-up resource
     * @return the created user resource
     */
    @PostMapping("/sign-up")
    @Operation(summary = "Sign up a new user", description = "Sign up a new user with the provided information and roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource resource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) return ResponseEntity.badRequest().build();
        var userEntity = user.get();
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(userEntity);
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    /**
     * Sign in a user.
     *
     * @param resource the sign-in resource
     * @return the authenticated user resource with token
     */
    @PostMapping("/sign-in")
    @Operation(summary = "Sign in a user", description = "Sign in a user with the provided email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed in successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource resource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var authenticatedUserResult = userCommandService.handle(signInCommand);
        if (authenticatedUserResult.isEmpty()) return ResponseEntity.notFound().build();
        var authenticatedUser = authenticatedUserResult.get();
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
            authenticatedUser.left,
            authenticatedUser.right
        );
        return ResponseEntity.ok(authenticatedUserResource);
    }
}

