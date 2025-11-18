package com.microservice.casedesk.interfaces.rest.controllers;

import com.microservice.casedesk.domain.model.aggregates.Case;
import com.microservice.casedesk.domain.model.commands.AddCaseNoteCommand;
import com.microservice.casedesk.domain.model.commands.AssignCaseCommand;
import com.microservice.casedesk.domain.model.commands.UpdateCaseStatusCommand;
import com.microservice.casedesk.domain.model.queries.GetAllOpenCasesQuery;
import com.microservice.casedesk.domain.model.queries.GetCaseByIdQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByDoctorAndStatusQuery;
import com.microservice.casedesk.domain.model.queries.GetCasesByPatientQuery;
import com.microservice.casedesk.domain.model.valueobjects.CaseStatus;
import com.microservice.casedesk.domain.services.CaseCommandService;
import com.microservice.casedesk.domain.services.CaseQueryService;
import com.microservice.casedesk.infrastructure.security.JwtAuthenticationToken;
import com.microservice.casedesk.interfaces.rest.resources.*;
import com.microservice.casedesk.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Tag(name = "Cases", description = "Case Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class CasesController {

    private final CaseCommandService caseCommandService;
    private final CaseQueryService caseQueryService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get my cases", description = "Returns cases for the authenticated user")
    public ResponseEntity<List<CaseResource>> getMyCases() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = authentication.getUserId();
        List<Case> cases = caseQueryService.handle(new GetCasesByPatientQuery(userId));

        List<CaseResource> resources = cases.stream()
                .map(CaseResourceAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get cases", description = "Returns cases based on filters for doctors and admins")
    public ResponseEntity<List<CaseResource>> getCases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") boolean assignedToMe,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {

        List<Case> cases;

        if (assignedToMe) {
            Long doctorId = authentication.getUserId();
            CaseStatus caseStatus = status != null ? CaseStatus.valueOf(status.toUpperCase()) : null;
            cases = caseQueryService.handle(new GetCasesByDoctorAndStatusQuery(doctorId, caseStatus));
        } else {
            cases = caseQueryService.handle(new GetAllOpenCasesQuery());
        }

        List<CaseResource> resources = cases.stream()
                .map(CaseResourceAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{caseId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get case by ID", description = "Returns detailed case information")
    public ResponseEntity<CaseDetailResource> getCaseById(
            @PathVariable Long caseId,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {

        Case caseEntity = caseQueryService.handle(new GetCaseByIdQuery(caseId))
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));

        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT"))) {
            if (!caseEntity.getPatientId().equals(authentication.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        CaseDetailResource resource = CaseDetailResourceAssembler.toResourceFromEntity(caseEntity);
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{caseId}/assign")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Assign case", description = "Assigns a case to a doctor")
    public ResponseEntity<CaseResource> assignCase(
            @PathVariable Long caseId,
            @RequestBody AssignCaseResource resource,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {

        Long doctorId = resource.doctorId() != null ? resource.doctorId() : authentication.getUserId();
        AssignCaseCommand command = AssignCaseCommandFromResourceAssembler.toCommandFromResource(
                caseId, new AssignCaseResource(doctorId), authentication.getUserId());

        Case updatedCase = caseCommandService.handle(command);
        CaseResource caseResource = CaseResourceAssembler.toResourceFromEntity(updatedCase);
        return ResponseEntity.ok(caseResource);
    }

    @PatchMapping("/{caseId}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Update case status", description = "Updates the status of a case")
    public ResponseEntity<CaseResource> updateCaseStatus(
            @PathVariable Long caseId,
            @RequestBody UpdateCaseStatusResource resource,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {

        UpdateCaseStatusCommand command = UpdateCaseStatusCommandFromResourceAssembler.toCommandFromResource(
                caseId, resource, authentication.getUserId());

        Case updatedCase = caseCommandService.handle(command);
        CaseResource caseResource = CaseResourceAssembler.toResourceFromEntity(updatedCase);
        return ResponseEntity.ok(caseResource);
    }

    @PostMapping("/{caseId}/notes")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Add case note", description = "Adds a note to a case")
    public ResponseEntity<CaseDetailResource> addCaseNote(
            @PathVariable Long caseId,
            @RequestBody AddCaseNoteResource resource,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {

        AddCaseNoteCommand command = AddCaseNoteCommandFromResourceAssembler.toCommandFromResource(
                caseId, resource, authentication.getUserId());

        Case updatedCase = caseCommandService.handle(command);
        CaseDetailResource caseResource = CaseDetailResourceAssembler.toResourceFromEntity(updatedCase);
        return ResponseEntity.ok(caseResource);
    }
}
