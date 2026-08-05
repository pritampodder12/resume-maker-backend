package com.resumebuilder.service.impl;

import com.resumebuilder.constant.AppConstants;
import com.resumebuilder.dto.request.CreateResumeRequest;
import com.resumebuilder.dto.request.UpdateResumeRequest;
import com.resumebuilder.dto.response.PagedResponse;
import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.entity.Resume;
import com.resumebuilder.entity.User;
import com.resumebuilder.exception.BadRequestException;
import com.resumebuilder.exception.ResourceNotFoundException;
import com.resumebuilder.exception.UnauthorizedException;
import com.resumebuilder.mapper.ResumeMapper;
import com.resumebuilder.repository.ResumeRepository;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.security.CustomUserDetails;
import com.resumebuilder.service.ResumeService;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public ResumeResponse createResume(CreateResumeRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Creating new resume for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Resume resume = Resume.builder()
                .user(user)
                .title(request.getTitle())
                .objective(request.getObjective())
                .templateName(request.getTemplateName())
                .active(true)
                .build();

        resume = resumeRepository.save(resume);
        log.info("Resume created successfully with ID: {}", resume.getId());

        return resumeMapper.toResumeResponse(resume);
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

        resume = resumeRepository.save(resume);
        log.info("Resume updated successfully: {}", id);

        return resumeMapper.toResumeResponse(resume);
    }

    @Override
    @Transactional
    public void deleteResume(UUID id) {
        UUID userId = getCurrentUserId();
        log.info("Soft deleting resume {} for user {}", id, userId);

        Resume resume = resumeRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        resume.setDeleted(true);
        resumeRepository.save(resume);
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
}