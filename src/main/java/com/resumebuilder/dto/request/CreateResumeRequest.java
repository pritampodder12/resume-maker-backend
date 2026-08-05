package com.resumebuilder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new resume")
public class CreateResumeRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Resume title", example = "Senior Software Engineer Resume", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Resume objective or summary", example = "Experienced software engineer seeking challenging opportunities...")
    private String objective;

    @Schema(description = "Template name to use", example = "modern")
    private String templateName;
}
