package com.resumebuilder.util;

import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.dto.response.SuggestionsResponse;
import com.resumebuilder.dto.response.resume.*;
import com.resumebuilder.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class SuggestionSectionResolver {
    public Object resolveSectionData(String section, ResumeResponse resumeResponse) {
        return switch (section.toUpperCase()) {
            case "EXPERIENCE" -> resumeResponse.getExperience();
//            case "EDUCATION" -> resumeResponse.getEducation();
            case "SKILLS" -> resumeResponse.getSkills();
            case "PROJECTS" -> resumeResponse.getProjects();
            case "CERTIFICATIONS" -> resumeResponse.getCertifications();
            case "SUMMARY" -> resumeResponse.getObjective();
            default -> throw new BadRequestException("Unsupported section: " + section);
        };
    }

    private List<SkillsResponse> flattenSkills(Map<String, List<SkillsResponse>> skillsByCategory) {
        return skillsByCategory.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    private List<UUID> resolveEntryIds(String section, ResumeResponse resumeResponse) {
        return switch (section.toUpperCase()) {
            case "EXPERIENCE" -> resumeResponse.getExperience().stream().map(ExperienceResponse::getId).toList();
            case "EDUCATION" -> resumeResponse.getEducation().stream().map(EducationResponse::getId).toList();
            case "SKILLS" -> resumeResponse.getSkills().stream().map(SkillsResponse::getId).toList();
            case "PROJECTS" -> resumeResponse.getProjects().stream().map(ProjectsResponse::getId).toList();
            case "CERTIFICATIONS" ->
                    resumeResponse.getCertifications().stream().map(CertificationsResponse::getId).toList();
            default -> throw new BadRequestException("Unsupported section: " + section);
        };
    }

    public void substituteEntryIds(SuggestionsResponse response, String section, ResumeResponse resumeResponse) {
        List<UUID> entryIds = resolveEntryIds(section, resumeResponse);
        response.resolveEntryIds(entryIds);
    }

}
