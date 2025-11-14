package com.microservice.iam.interfaces.rest.dto.request;

import java.util.Set;

public record UpdateUserRolesRequest(
    Set<String> roles
) {}

