package com.resumebuilder.dto.request;

import com.resumebuilder.dto.request.resume.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Schema(description = "Candidate Name of Resume", example = "John Doe")
    private String candidateName;

    @Schema(description = "Resume objective or summary", example = "Experienced software engineer seeking challenging opportunities...")
    private String objective;

    @Schema(description = "Template name to use", example = "modern")
    private String templateName;

    @Valid
    @Schema(description = "Education entries")
    @Builder.Default
    private List<EducationRequest> education = new ArrayList<>();;

    @Valid
    @Schema(description = "Experience entries")
    @Builder.Default
    private List<ExperienceRequest> experience = new ArrayList<>();;

    @Valid
    @Schema(description = "Certifications entries")
    @Builder.Default
    private List<CertificationsRequest> certifications = new ArrayList<>();;

    @Valid
    @Schema(description = "Projects entries")
    @Builder.Default
    private List<ProjectsRequest> projects = new ArrayList<>();;

    @Valid
    @Schema(description = "Skills entries")
    @Builder.Default
    private List<SkillsRequest> skills = new ArrayList<>();;

    @Valid
    @Schema(description = "Resume Section")
    @Builder.Default
    private List<SectionRequest> sections = new ArrayList<>();;

}
