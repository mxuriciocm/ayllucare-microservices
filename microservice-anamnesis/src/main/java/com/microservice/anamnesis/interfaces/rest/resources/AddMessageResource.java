package com.microservice.anamnesis.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST resource for adding a message to an anamnesis session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageResource {

    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 2000, message = "Message must be between 1 and 2000 characters")
    private String content;
}

