package com.resumebuilder.dto.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skills data")
public class SkillsResponse {
    private String name;
    private Integer proficiencyLevel;
    private Integer yearsOfExperience;
    private List<String> description;
    private Integer sortOrder = 0;
}
