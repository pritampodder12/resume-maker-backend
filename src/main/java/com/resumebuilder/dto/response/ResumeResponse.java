package com.resumebuilder.dto.response;

import com.resumebuilder.dto.response.resume.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resume response data")
public class ResumeResponse {

    @Schema(description = "Resume ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Resume title", example = "Senior Software Engineer Resume")
    private String title;

    @Schema(description = "Resume slug for URL", example = "senior-software-engineer-resume")
    private String slug;

    @Schema(description = "Resume objective")
    private String objective;

    @Schema(description = "Template name", example = "modern")
    private String templateName;

    @Schema(description = "Whether this is the active resume", example = "true")
    private Boolean active;

    @Schema(description = "Resume creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date")
    private LocalDateTime updatedAt;

    @Schema(description = "Last published date")
    private LocalDateTime lastPublishedAt;

    @Schema(description = "Current publish version", example = "1")
    private Integer publishVersion;

    private List<EducationResponse> education;

    private List<ExperienceResponse> experience;

    private List<CertificationsResponse> certifications;

    private List<ProjectsResponse> projects;

    private List<SkillsResponse> skills;

    private List<SectionResponse> sections;
}
