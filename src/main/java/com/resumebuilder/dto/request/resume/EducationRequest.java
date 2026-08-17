package com.resumebuilder.dto.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Education entry for resume creation")
public class EducationRequest {

    @NotBlank(message = "Institution name is required")
    @Schema(description = "Institution / school/university name", example = "Indian Institute of Technology, Kharagpur", requiredMode = Schema.RequiredMode.REQUIRED)
    private String institutionName;

    @NotBlank(message = "Degree is required")
    @Schema(description = "Degree name", example = "Bachelor of Technology in Computer Science", requiredMode = Schema.RequiredMode.REQUIRED)
    private String degree;

    @Schema(description = "Field of study (major)", example = "Computer Science and Engineering")
    private String fieldOfStudy;

    @Schema(description = "Location of the institution", example = "Kolkata, India")
    private String location;

    @Schema(description = "Start date of education", example = "2020-08-01")
    private String startDate;

    @Schema(description = "End date (omit if currently studying)", example = "2024-06-01")
    private String endDate;

    @Schema(description = "Currently studying at this institution", example = "true")
    private boolean current;

    @Schema(description = "Grade Point Average (optional)")
    private String gpa;

    @Schema(description = "Description / additional details (multi-line notes)", example = "• Dean's List, Fall 2022\n• Graduated with Honors")
    private List<String> description;

    @Schema(description = "Sort order (lower numbers appear first)", example = "1")
    private Integer sortOrder;
}
