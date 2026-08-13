package com.resumebuilder.dto.request;

import com.resumebuilder.dto.request.resume.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating a resume")
public class UpdateResumeRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Resume title", example = "Updated: Senior Software Engineer Resume")
    private String title;

    @Schema(description = "Name of the person this resume is for", example = "Alex Chen")
    private String candidateName;

    @Schema(description = "Resume objective or summary", example = "Experienced software engineer seeking challenging opportunities...")
    private String objective;

    @Schema(description = "Template name to use", example = "classic")
    private String templateName;

    @Schema(description = "Whether this is the active resume", example = "false")
    private Boolean active;

    @Valid
    @Schema(description = "Work experience entries (replaces all existing entries if provided)")
    private List<ExperienceRequest> experience;

    @Valid
    @Schema(description = "Education entries (replaces all existing entries if provided)")
    private List<EducationRequest> education;

    @Valid
    @Schema(description = "Projects (replaces all existing entries if provided)")
    private List<ProjectsRequest> projects;

    @Valid
    @Schema(description = "Skills (replaces all existing entries if provided)")
    private List<SkillsRequest> skills;

    @Valid
    @Schema(description = "Certifications (replaces all existing entries if provided)")
    private List<CertificationsRequest> certifications;

    @Valid
    @Schema(description = "Custom sections (replaces all existing entries if provided)")
    private List<SectionRequest> sections;
}
