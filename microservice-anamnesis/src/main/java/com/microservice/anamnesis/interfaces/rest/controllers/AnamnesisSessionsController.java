package com.microservice.anamnesis.interfaces.rest.controllers;

import com.microservice.anamnesis.domain.model.commands.CompleteAnamnesisSessionCommand;
import com.microservice.anamnesis.domain.model.queries.GetSessionByIdQuery;
import com.microservice.anamnesis.domain.model.queries.GetSessionsByUserIdQuery;
import com.microservice.anamnesis.domain.services.AnamnesisCommandService;
import com.microservice.anamnesis.domain.services.AnamnesisQueryService;
import com.microservice.anamnesis.infrastructure.security.JwtAuthenticationToken;
import com.microservice.anamnesis.interfaces.rest.resources.*;
import com.microservice.anamnesis.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for anamnesis session operations.
 */
@RestController
@RequestMapping("/api/v1/anamnesis")
@Tag(name = "Anamnesis", description = "Anamnesis session management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnamnesisSessionsController {

    private final AnamnesisCommandService commandService;
    private final AnamnesisQueryService queryService;

    public AnamnesisSessionsController(AnamnesisCommandService commandService, AnamnesisQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(
            summary = "Start new anamnesis session",
            description = "Creates a new anamnesis session for the authenticated patient",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Session created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "409", description = "User has not given consent for AI processing")
            }
    )
    public ResponseEntity<AnamnesisSessionResource> createSession(
            @Valid @RequestBody CreateAnamnesisSessionResource resource) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = authentication.getUserId();
        var command = StartAnamnesisSessionCommandFromResourceAssembler.toCommandFromResource(userId, resource);

        var session = commandService.handle(command);

        if (session.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var sessionResource = AnamnesisSessionResourceAssembler.toResourceFromEntity(session.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResource);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(
            summary = "Add message to session",
            description = "Adds a patient message to the session and receives an AI response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Not authorized to access this session"),
                    @ApiResponse(responseCode = "404", description = "Session not found")
            }
    )
    public ResponseEntity<AnamnesisSessionDetailResource> addMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody AddMessageResource resource) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = authentication.getUserId();
        var command = AddMessageToSessionCommandFromResourceAssembler.toCommandFromResource(sessionId, userId, resource);

        var session = commandService.handle(command);

        if (session.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var detailResource = AnamnesisSessionDetailResourceAssembler.toResourceFromEntity(session.get());
        return ResponseEntity.ok(detailResource);
    }

    @PostMapping("/sessions/{sessionId}/complete")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @Operation(
            summary = "Complete anamnesis session",
            description = "Completes the session and generates a structured anamnesis summary",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session completed successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Not authorized to complete this session"),
                    @ApiResponse(responseCode = "404", description = "Session not found")
            }
    )
    public ResponseEntity<AnamnesisSessionResource> completeSession(
            @PathVariable Long sessionId) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = authentication.getUserId();
        var command = new CompleteAnamnesisSessionCommand(sessionId, userId);

        var session = commandService.handle(command);

        if (session.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var sessionResource = AnamnesisSessionResourceAssembler.toResourceFromEntity(session.get());
        return ResponseEntity.ok(sessionResource);
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(
            summary = "Get user sessions",
            description = "Retrieves all anamnesis sessions for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<List<AnamnesisSessionResource>> getUserSessions() {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = authentication.getUserId();
        var query = new GetSessionsByUserIdQuery(userId);
        var sessions = queryService.handle(query);

        var resources = sessions.stream()
                .map(AnamnesisSessionResourceAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(
            summary = "Get session details",
            description = "Retrieves full session details including conversation messages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Not authorized to access this session"),
                    @ApiResponse(responseCode = "404", description = "Session not found")
            }
    )
    public ResponseEntity<AnamnesisSessionDetailResource> getSession(
            @PathVariable Long sessionId) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var query = new GetSessionByIdQuery(sessionId);
        var session = queryService.handle(query);

        if (session.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = authentication.getUserId();
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (isPatient && !session.get().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var detailResource = AnamnesisSessionDetailResourceAssembler.toResourceFromEntity(session.get());
        return ResponseEntity.ok(detailResource);
    }

    @GetMapping("/sessions/{sessionId}/summary")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(
            summary = "Get session summary",
            description = "Retrieves the structured anamnesis summary for a completed session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Not authorized to access this session"),
                    @ApiResponse(responseCode = "404", description = "Session not found or summary not available")
            }
    )
    public ResponseEntity<AnamnesisSummaryResource> getSessionSummary(
            @PathVariable Long sessionId) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var query = new GetSessionByIdQuery(sessionId);
        var session = queryService.handle(query);

        if (session.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = authentication.getUserId();
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (isPatient && !session.get().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!session.get().hasSummary()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var summaryResource = AnamnesisSummaryResourceAssembler.toResourceFromEntity(session.get());
        return ResponseEntity.ok(summaryResource);
    }
}

