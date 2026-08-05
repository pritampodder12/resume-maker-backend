package com.resumebuilder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating a resume")
public class UpdateResumeRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Resume title", example = "Updated: Senior Software Engineer Resume")
    private String title;

    @Schema(description = "Resume objective or summary", example = "Experienced software engineer seeking challenging opportunities...")
    private String objective;

    @Schema(description = "Template name to use", example = "classic")
    private String templateName;

    @Schema(description = "Whether this is the active resume", example = "false")
    private Boolean active;
}
