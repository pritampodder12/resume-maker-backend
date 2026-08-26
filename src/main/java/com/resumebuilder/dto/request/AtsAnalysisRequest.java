package com.resumebuilder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for ATS Analysis")
public class AtsAnalysisRequest {

    @NotBlank(message = "Job Description is required")
    @Schema(description = "Job Description", example = "Job description posted by company", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobDescription;
}
