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
@Schema(description = "Work experience entry for resume creation")
public class ExperienceRequest {

    @NotBlank(message = "Company name is required")
    @Schema(description = "Company name", example = "Google", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @NotBlank(message = "Position is required")
    @Schema(description = "Position", example = "Senior Software Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String position;

    @Schema(description = "Location", example = "Kolkata, India")
    private String location;

    @Schema(description = "Employment Type", example = "Full-time")
    private String employmentType;

    @Schema(description = "Start date", example = "2022-01-01")
    private String startDate;

    @Schema(description = "End date (omit if current)", example = "2024-06-01")
    private String endDate;

    @Schema(description = "Currently working here", example = "false")
    private boolean current;

    @Schema(description = "Description / responsibilities")
    private List<String> description;

    @Schema(description = "Highlights")
    private String highlights;

    @Schema(description = "Sort order", example = "1")
    private Integer sortOrder;
}
