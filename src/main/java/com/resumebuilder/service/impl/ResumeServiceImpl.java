package com.resumebuilder.service.impl;

import com.resumebuilder.constant.AppConstants;
import com.resumebuilder.dto.request.CreateResumeRequest;
import com.resumebuilder.dto.request.UpdateResumeRequest;
import com.resumebuilder.dto.response.AtsAnalysisResponse;
import com.resumebuilder.dto.response.PagedResponse;
import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.dto.response.SuggestionsResponse;
import com.resumebuilder.entity.*;
import com.resumebuilder.exception.BadRequestException;
import com.resumebuilder.exception.ResourceNotFoundException;
import com.resumebuilder.exception.UnauthorizedException;
import com.resumebuilder.mapper.AtsAnalysisMapper;
import com.resumebuilder.mapper.ResumeMapper;
import com.resumebuilder.repository.AtsAnalyseRepository;
import com.resumebuilder.repository.ResumeRepository;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.security.CustomUserDetails;
import com.resumebuilder.service.AiResumeService;
import com.resumebuilder.service.PdfExtractionService;
import com.resumebuilder.service.ResumeService;
import com.resumebuilder.util.SuggestionSectionResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AtsAnalyseRepository atsAnalyseRepository;
    private final ResumeMapper resumeMapper;
    private final AtsAnalysisMapper atsAnalysisMapper;
    private final PdfExtractionService pdfExtractionService;
    private final AiResumeService aiResumeService;
    private final SuggestionSectionResolver suggestionSectionResolver;

    @Override
    @Transactional
    public ResumeResponse createResume(CreateResumeRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Creating new resume for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Resume resume = Resume.builder()
                .user(user)
                .candidateName(request.getCandidateName())
                .title(request.getTitle())
                .objective(request.getObjective())
                .templateName(request.getTemplateName())
                .active(true)
                .build();

        // Map Education section
//        if (request.getEducation() != null && !request.getEducation().isEmpty()) {
        List<Education> educationList = request.getEducation().stream()
                .map(req -> Education.builder()
                        .institutionName(req.getInstitutionName())
                        .degree(req.getDegree())
                        .fieldOfStudy(req.getFieldOfStudy())
                        .location(req.getLocation())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .current(req.isCurrent())
                        .gpa(req.getGpa())
                        .description(req.getDescription())
                        .sortOrder(req.getSortOrder())
                        .resume(resume)
                        .build())
                .toList();
        resume.setEducation(new HashSet<>(educationList));
        log.debug("Mapped {} education entries", educationList.size());
//        }

        // Map Experience section
//        if (request.getExperience() != null && !request.getExperience().isEmpty()) {
        List<Experience> experiences = request.getExperience().stream()
                .map(req -> Experience.builder()
                        .companyName(req.getCompanyName())
                        .position(req.getPosition())
                        .location(req.getLocation())
                        .employmentType(req.getEmploymentType())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .current(req.isCurrent())
                        .description(req.getDescription())
                        .highlights(req.getHighlights())
                        .sortOrder(req.getSortOrder())
                        .resume(resume)
                        .build())
                .toList();
        resume.setExperience(new HashSet<>(experiences));
        log.debug("Mapped {} experience entries", experiences.size());
//        }

        // Map Certifications section
//        if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
        List<Certification> certifications = request.getCertifications().stream()
                .map(req -> Certification.builder()
                        .name(req.getName())
                        .issuingOrganization(req.getIssuingOrganization())
                        .credentialId(req.getCredentialId())
                        .credentialUrl(req.getCredentialUrl())
                        .issueDate(req.getIssueDate())
                        .expirationDate(req.getExpirationDate())
                        .doesNotExpire(req.isDoesNotExpire())
                        .description(req.getDescription())
                        .sortOrder(req.getSortOrder())
                        .resume(resume)
                        .build())
                .toList();
        resume.setCertifications(new HashSet<>(certifications));
        log.debug("Mapped {} certification entries", certifications.size());
//        }

        // Map Projects section
//        if (request.getProjects() != null && !request.getProjects().isEmpty()) {
        List<Project> projects = request.getProjects().stream()
                .map(req -> Project.builder()
                        .name(req.getName())
                        .description(req.getDescription())
                        .technologies(req.getTechnologies())
                        .projectUrl(req.getProjectUrl())
                        .githubUrl(req.getGithubUrl())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .current(req.isCurrent())
                        .sortOrder(req.getSortOrder())
                        .resume(resume)
                        .build())
                .toList();
        resume.setProjects(new HashSet<>(projects));
        log.debug("Mapped {} project entries", projects.size());
//        }

        // Map Skills section
//        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
        List<Skill> skills = request.getSkills().stream()
                .map(req -> Skill.builder()
                        .name(req.getName())
                        .category(req.getCategory())
                        .proficiencyLevel(req.getProficiencyLevel())
                        .yearsOfExperience(req.getYearsOfExperience())
                        .description(req.getDescription())
                        .sortOrder(req.getSortOrder())
                        .resume(resume)
                        .build())
                .toList();
        resume.setSkills(new HashSet<>(skills));
        log.debug("Mapped {} skill entries", skills.size());
//        }

        // Map Sections (custom sections)
//        if (request.getSections() != null && !request.getSections().isEmpty()) {
        List<com.resumebuilder.entity.ResumeSection> sections = request.getSections().stream()
                .map(req -> com.resumebuilder.entity.ResumeSection.builder()
                        .sectionType(req.getSectionType())
                        .sectionOrder(req.getSectionOrder())
                        .title(req.getTitle())
                        .content(req.getContent())
                        .visible(req.isVisible())
                        .resume(resume)
                        .build())
                .toList();
        resume.setSections(new HashSet<>(sections));
        log.debug("Mapped {} section entries", sections.size());
//        }

        Resume saved = resumeRepository.save(resume);
        log.info("Resume created successfully with ID: {}", saved.getId());

        return resumeMapper.toResumeResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Fetching resume {} for user {}", id, userId);

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        return resumeMapper.toResumeResponse(resume);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ResumeResponse> getResumes(int page, int size, String sortBy, String sortDir) {
        UUID userId = getCurrentUserId();
        log.debug("Fetching resumes for user {} - page: {}, size: {}", userId, page, size);

        Sort sort = sortDir.equalsIgnoreCase(AppConstants.DEFAULT_SORT_DIRECTION)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Resume> resumePage = resumeRepository.findByUserIdAndDeletedFalse(userId, pageable);

        return PagedResponse.of(
                resumeMapper.toResumeResponseList(resumePage.getContent()),
                resumePage.getNumber(),
                resumePage.getSize(),
                resumePage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public ResumeResponse updateResume(UUID id, UpdateResumeRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Updating resume {} for user {}", id, userId);

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        if (request.getCandidateName() != null) {
            resume.setCandidateName(request.getCandidateName());
        }

        if (request.getTitle() != null) {
            resume.setTitle(request.getTitle());
        }
        if (request.getObjective() != null) {
            resume.setObjective(request.getObjective());
        }
        if (request.getTemplateName() != null) {
            resume.setTemplateName(request.getTemplateName());
        }
        if (request.getActive() != null) {
            if (request.getActive() && !resume.isActive()) {
                resumeRepository.findByUserIdAndActiveTrueAndDeletedFalse(userId)
                        .filter(r -> !r.getId().equals(id))
                        .ifPresent(activeResume -> {
                            activeResume.setActive(false);
                            resumeRepository.save(activeResume);
                        });
            }
            resume.setActive(request.getActive());
        }

        if (request.getEducation() != null) {
            resume.getEducation().clear();
            request.getEducation().forEach(req -> resume.getEducation().add(
                    Education.builder()
                            .institutionName(req.getInstitutionName())
                            .degree(req.getDegree())
                            .fieldOfStudy(req.getFieldOfStudy())
                            .location(req.getLocation())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .current(req.isCurrent())
                            .gpa(req.getGpa())
                            .description(req.getDescription())
                            .sortOrder(req.getSortOrder())
                            .resume(resume)
                            .build()
            ));
        }

        if (request.getExperience() != null) {
            resume.getExperience().clear();
            request.getExperience().forEach(req -> resume.getExperience().add(
                    Experience.builder()
                            .companyName(req.getCompanyName())
                            .position(req.getPosition())
                            .location(req.getLocation())
                            .employmentType(req.getEmploymentType())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .current(req.isCurrent())
                            .description(req.getDescription())
                            .highlights(req.getHighlights())
                            .sortOrder(req.getSortOrder())
                            .resume(resume)
                            .build()
            ));
        }

        if (request.getProjects() != null) {
            resume.getProjects().clear();
            request.getProjects().forEach(req -> resume.getProjects().add(
                    Project.builder()
                            .name(req.getName())
                            .description(req.getDescription())
                            .technologies(req.getTechnologies())
                            .projectUrl(req.getProjectUrl())
                            .githubUrl(req.getGithubUrl())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .current(req.isCurrent())
                            .sortOrder(req.getSortOrder())
                            .resume(resume)
                            .build()
            ));
        }

        if (request.getSkills() != null) {
            resume.getSkills().clear();
            request.getSkills().forEach(req -> resume.getSkills().add(
                    Skill.builder()
                            .name(req.getName())
                            .category(req.getCategory())
                            .proficiencyLevel(req.getProficiencyLevel())
                            .yearsOfExperience(req.getYearsOfExperience())
                            .description(req.getDescription())
                            .sortOrder(req.getSortOrder())
                            .resume(resume)
                            .build()
            ));
        }
        if (request.getCertifications() != null) {
            resume.getCertifications().clear();
            request.getCertifications().forEach(req -> resume.getCertifications().add(
                    Certification.builder()
                            .name(req.getName())
                            .issuingOrganization(req.getIssuingOrganization())
                            .credentialId(req.getCredentialId())
                            .credentialUrl(req.getCredentialUrl())
                            .issueDate(req.getIssueDate())
                            .expirationDate(req.getExpirationDate())
                            .doesNotExpire(req.isDoesNotExpire())
                            .description(req.getDescription())
                            .sortOrder(req.getSortOrder())
                            .resume(resume)
                            .build()
            ));
        }

        if (request.getSections() != null) {
            resume.getSections().clear();
            request.getSections().forEach(req -> resume.getSections().add(
                    ResumeSection.builder()
                            .sectionType(req.getSectionType())
                            .sectionOrder(req.getSectionOrder())
                            .title(req.getTitle())
                            .content(req.getContent())
                            .visible(req.isVisible())
                            .resume(resume)
                            .build()
            ));
        }

        Resume savedResume = resumeRepository.save(resume);
        log.info("Resume updated successfully: {}", id);

        return resumeMapper.toResumeResponse(savedResume);
    }

    @Override
    @Transactional
    public void deleteResume(UUID id) {
        UUID userId = getCurrentUserId();
        log.info("Soft deleting resume {} for user {}", id, userId);

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

//        For Soft Delete
//        resume.setDeleted(true);
//        resumeRepository.save(resume);

        resumeRepository.delete(resume);

        log.info("Resume soft deleted successfully: {}", id);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ResumeResponse createResumeFromPdf(MultipartFile file) {
        String rawText = pdfExtractionService.extractText(file);
        ResumeResponse parsedResume = aiResumeService.parseResume(rawText);
        log.info("AI parsing successful, returning parsed data without saving");
        return parsedResume;
    }

    @Override
    @Transactional
    public AtsAnalysisResponse analyseResume(UUID id, String jobDescription) {
        UUID userId = getCurrentUserId();

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        // Reuse an existing analysis for the same resume + JD instead of
        // creating a duplicate row and re-calling the AI unnecessarily.
        Optional<AtsAnalysis> existing = atsAnalyseRepository
                .findFirstByResumeIdAndJobDescriptionOrderByCreatedAtDesc(id, jobDescription);

//        if (existing.isPresent() && isStillValid(existing.get(), resume)) {
//            log.info("Reusing existing ATS analysis {} for resume {}", existing.get().getId(), id);
//            return atsAnalysisMapper.toAtsAnalysisResponse(existing.get());
//        }
//
//        if (existing.isPresent()) {
//            log.info("Existing ATS analysis {} is stale (resume updated after analysis) — re-analysing", existing.get().getId());
//        }

        if (existing.isPresent()) {
            log.info("Reusing existing ATS analysis {} for resume {}", existing.get().getId(), id);
            return atsAnalysisMapper.toAtsAnalysisResponse(existing.get());
        }

        AtsAnalysisResponse response = aiResumeService.analyseJobMatch(resumeMapper.toResumeResponse(resume), jobDescription);

        AtsAnalysis atsAnalysis = AtsAnalysis.builder()
                .resume(resume)
                .jobDescription(jobDescription)
                .overallScore(response.getAtsScore().getOverall())
                .keywordsScore(response.getAtsScore().getKeyword())
                .formattingScore(response.getAtsScore().getFormatting())
                .impactScore(response.getAtsScore().getImpact())
                .build();

        List<AtsAnalysisKeyword> atsAnalysisKeywords = response.getExtractedKeywords().stream()
                .map(keywordData -> AtsAnalysisKeyword.builder()
                        .keyword(keywordData.getKeyword())
                        .matched(keywordData.isMatched())
                        .atsAnalysis(atsAnalysis)
                        .build())
                .toList();
        atsAnalysis.setKeywords(new HashSet<>(atsAnalysisKeywords));
        log.debug("Mapped {} ats analysis keyword entries", atsAnalysisKeywords.size());

        AtsAnalysis saved = atsAnalyseRepository.save(atsAnalysis);
        log.info("Ats Analysis Data created with ID: {}", saved.getId());

        response.setAnalysisId(saved.getId());
        return response;
    }

    @Override
    @Transactional
    public SuggestionsResponse generateSuggestion(UUID resumeId, UUID analysisId, String section) {
        log.info("Generating {} suggestions for resume {} (analysis {})", section, resumeId, analysisId);

        UUID userId = getCurrentUserId();

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

        AtsAnalysis atsAnalysis = atsAnalyseRepository.findByIdAndResumeId(analysisId, resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("AtsAnalysis", "id", analysisId));

        List<String> missingKeyword = atsAnalysis.getKeywords().stream()
                .filter(k -> !k.isMatched())
                .map(AtsAnalysisKeyword::getKeyword)
                .toList();

        ResumeResponse resumeResponse = resumeMapper.toResumeResponse(resume);

        Object sectionData = suggestionSectionResolver.resolveSectionData(section, resumeResponse);

        SuggestionsResponse response = aiResumeService.generateSuggestions(sectionData, section, atsAnalysis.getJobDescription(), missingKeyword);

//        suggestionSectionResolver.substituteEntryIds(response, section, resumeResponse);

        return response;
    }

    private boolean isStillValid(AtsAnalysis analysis, Resume resume) {
        return resume.getUpdatedAt() == null
                || !resume.getUpdatedAt().isAfter(analysis.getCreatedAt());
    }
}
