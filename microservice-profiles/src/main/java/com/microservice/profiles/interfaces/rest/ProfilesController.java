package com.microservice.profiles.interfaces.rest;

import com.microservice.profiles.domain.model.commands.SignConsentCommand;
import com.microservice.profiles.domain.model.queries.GetAllProfilesQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByIdQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.microservice.profiles.domain.services.ProfileCommandService;
import com.microservice.profiles.domain.services.ProfileQueryService;
import com.microservice.profiles.interfaces.rest.resources.ProfileResource;
import com.microservice.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.microservice.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.microservice.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Patient Profile Management Endpoints for AylluCare")
public class ProfilesController {
    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;

    public ProfilesController(ProfileQueryService profileQueryService, ProfileCommandService profileCommandService) {
        this.profileQueryService = profileQueryService;
        this.profileCommandService = profileCommandService;
    }

    /**
     * Get a profile by profile ID
     * @param profileId the ID of the profile to retrieve
     * @return ResponseEntity containing the ProfileResource or an error response
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "Get a profile by profile ID", description = "Retrieves a patient profile by its profile ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
        var query = new GetProfileByIdQuery(profileId);
        var profile = profileQueryService.handle(query);
        if (profile.isEmpty()) { return ResponseEntity.notFound().build(); }
        var resource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Get a profile by user ID from IAM service
     * @param userId the ID of the user from IAM whose profile is to be retrieved
     * @return ResponseEntity containing the ProfileResource or an error response
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get a profile by user ID", description = "Retrieves a patient profile by the associated user ID from IAM service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found for this user")})
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable Long userId) {
        var query = new GetProfileByUserIdQuery(userId);
        var profile = profileQueryService.handle(query);
        if (profile.isEmpty()) { return ResponseEntity.notFound().build(); }
        var resource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Get all profiles
     * @return List of ProfileResource
     */
    @GetMapping
    @Operation(summary = "Get all profiles", description = "Retrieves all patient profiles in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles found"),
            @ApiResponse(responseCode = "204", description = "No profiles found")})
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var query = new GetAllProfilesQuery();
        var profiles = profileQueryService.handle(query);
        if (profiles.isEmpty()) { return ResponseEntity.noContent().build(); }
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profileResources);
    }

    /**
     * Update a profile by profile ID
     * @param profileId the ID of the profile to be updated
     * @param resource the resource containing the updated profile data
     * @return ResponseEntity containing the updated ProfileResource or an error response
     */
    @PatchMapping(value = "/{profileId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a profile", description = "Updates patient medical and personal information. All fields are optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "400", description = "Invalid profile data")})
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable Long profileId, @RequestBody UpdateProfileResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(resource, profileId);
        var updatedProfile = profileCommandService.handle(command);
        if (updatedProfile.isEmpty()) { return ResponseEntity.notFound().build(); }
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Sign consent for data sharing and AI processing
     * @param profileId the ID of the profile
     * @return ResponseEntity containing the updated ProfileResource with consent information
     */
    @PostMapping("/{profileId}/consent")
    @Operation(summary = "Sign consent", description = "Patient signs consent for data sharing and AI processing (GDPR/HIPAA compliance)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consent signed successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> signConsent(@PathVariable Long profileId) {
        var command = new SignConsentCommand(profileId);
        var updatedProfile = profileCommandService.handle(command);
        if (updatedProfile.isEmpty()) { return ResponseEntity.notFound().build(); }
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());
        return ResponseEntity.ok(profileResource);
    }
}
