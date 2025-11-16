package com.microservice.triage.interfaces.rest.controllers;

import com.microservice.triage.domain.model.aggregates.TriageResult;
import com.microservice.triage.domain.model.queries.GetAllTriagesQuery;
import com.microservice.triage.domain.model.queries.GetTriageByIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageBySessionIdQuery;
import com.microservice.triage.domain.model.queries.GetTriageByUserIdQuery;
import com.microservice.triage.domain.services.TriageQueryService;
import com.microservice.triage.infrastructure.security.JwtAuthenticationToken;
import com.microservice.triage.interfaces.rest.resources.TriageResultResource;
import com.microservice.triage.interfaces.rest.transform.TriageResultResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for triage operations.
 */
@RestController
@RequestMapping("/api/v1/triage")
@Tag(name = "Triage", description = "Medical triage assessment endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TriageController {

    private final TriageQueryService queryService;

    public TriageController(TriageQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(
            summary = "Get triage results by user ID",
            description = "Retrieves all triage results for a specific user (Doctor/Admin only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Triage results retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Doctor/Admin role required")
            }
    )
    public ResponseEntity<List<TriageResultResource>> getTriageByUserId(@PathVariable Long userId) {
        var query = new GetTriageByUserIdQuery(userId);
        List<TriageResult> triages = queryService.handle(query);

        List<TriageResultResource> resources = triages.stream()
                .map(TriageResultResourceAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(
            summary = "Get triage result by ID",
            description = "Retrieves a specific triage result by its ID (Doctor/Admin only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Triage result retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Doctor/Admin role required"),
                    @ApiResponse(responseCode = "404", description = "Triage result not found")
            }
    )
    public ResponseEntity<TriageResultResource> getTriageById(@PathVariable Long id) {
        var query = new GetTriageByIdQuery(id);
        Optional<TriageResult> triage = queryService.handle(query);

        return triage.map(result -> ResponseEntity.ok(
                TriageResultResourceAssembler.toResourceFromEntity(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(
            summary = "Get triage result by anamnesis session ID",
            description = "Retrieves a triage result generated from a specific anamnesis session (Doctor/Admin only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Triage result retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Doctor/Admin role required"),
                    @ApiResponse(responseCode = "404", description = "Triage result not found for this session")
            }
    )
    public ResponseEntity<TriageResultResource> getTriageBySessionId(@PathVariable Long sessionId) {
        var query = new GetTriageBySessionIdQuery(sessionId);
        Optional<TriageResult> triage = queryService.handle(query);

        return triage.map(result -> ResponseEntity.ok(
                TriageResultResourceAssembler.toResourceFromEntity(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all triage results",
            description = "Retrieves all triage results in the system (Admin only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Triage results retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
            }
    )
    public ResponseEntity<List<TriageResultResource>> getAllTriages() {
        var query = new GetAllTriagesQuery();
        List<TriageResult> triages = queryService.handle(query);

        List<TriageResultResource> resources = triages.stream()
                .map(TriageResultResourceAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}

