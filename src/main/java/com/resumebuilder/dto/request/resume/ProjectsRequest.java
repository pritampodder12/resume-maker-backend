package com.resumebuilder.dto.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Project entry for resume creation")
public class ProjectsRequest {

    @NotBlank(message = "Project name is required")
    @Schema(description = "Project name", example = "E-Commerce Platform", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the project (multi-line notes)", example = "Built a full-featured e-commerce platform with React and Node.js")
    private List<String> description;

    @Schema(description = "Technologies used (comma-separated for display)", example = "React, Node.js, MongoDB, AWS")
    private String technologies;

    @Schema(description = "Project URL (optional)", example = "https://github.com/user/project-name")
    private String projectUrl;

    @Schema(description = "GitHub repository URL (optional)", example = "https://github.com/user/project-name")
    private String githubUrl;

    @Schema(description = "Start date", example = "2022-06-01")
    private String startDate;

    @Schema(description = "End date (omitted if still in progress)", example = "2023-12-31")
    private String endDate;

    @Schema(description = "Currently working on this project", example = "false")
    private boolean current;

    @Schema(description = "Sort order (lower numbers appear first)", example = "1")
    private Integer sortOrder;
}
