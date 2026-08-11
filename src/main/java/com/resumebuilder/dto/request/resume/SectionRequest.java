package com.resumebuilder.dto.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dynamic resume section for custom resume layout")
public class SectionRequest {

    @NotBlank(message = "Section type is required")
    @Schema(description = "Section type identifier (e.g., 'CUSTOM_NOTE', 'SKILLS_SUMMARY')", example = "CUSTOM_NOTE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sectionType;

    @NotBlank(message = "Section title is required")
    @Schema(description = "Section title displayed in resume", example = "Other Skills & Achievements", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Section content / body text", example = "• Swift\n• Kotlin\n• Hibernate\n• Lambda Expressions")
    private String content;

    @Schema(description = "Whether this section is visible in the resume", example = "true")
    private boolean visible;

    @Schema(description = "Order in which section appears relative to other sections", example = "10")
    private Integer sectionOrder;
}
