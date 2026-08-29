package com.resumebuilder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequest {

    @NotBlank
    private String section; // "EXPERIENCE" | "EDUCATION" | "SKILLS" | "PROJECTS" | "CERTIFICATIONS"
}
