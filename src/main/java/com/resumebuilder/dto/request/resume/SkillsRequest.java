package com.resumebuilder.dto.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Skill entry for resume creation")
public class SkillsRequest {

    @NotBlank(message = "Skill name is required")
    @Schema(description = "Skill name", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Category of the skill (optional)", example = "Programming Languages")
    private String category;

    @Schema(description = "Proficiency level (1-100)", example = "90")
    private Integer proficiencyLevel;

    @Schema(description = "Years of experience", example = "5")
    private Integer yearsOfExperience;

    @Schema(description = "Description / additional details about the skill", example = "Advanced knowledge, production experience")
    private List<String> description;

    @Schema(description = "Sort order (lower numbers appear first)", example = "1")
    private Integer sortOrder;
}
