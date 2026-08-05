package com.resumebuilder.service.impl;

import com.resumebuilder.dto.response.DashboardResponse;
import com.resumebuilder.entity.DashboardActivity;
import com.resumebuilder.entity.User;
import com.resumebuilder.exception.UnauthorizedException;
import com.resumebuilder.repository.DashboardActivityRepository;
import com.resumebuilder.repository.ResumeRepository;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.security.CustomUserDetails;
import com.resumebuilder.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final DashboardActivityRepository dashboardActivityRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        UUID userId = getCurrentUserId();
        log.debug("Fetching dashboard for user: {}", userId);

        long totalResumes = resumeRepository.countByUserId(userId);
        long activeResumes = resumeRepository.findActiveResumeByUserId(userId)
                .map(r -> 1L)
                .orElse(0L);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        int profileCompletion = calculateProfileCompletion(user);

        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        Page<DashboardActivity> activitiesPage = dashboardActivityRepository
                .findByUserIdAndCreatedAtAfter(userId, lastWeek, PageRequest.of(0, 10));

        List<DashboardResponse.ActivityItem> recentActivities = activitiesPage.getContent().stream()
                .map(this::mapToActivityItem)
                .toList();

        return DashboardResponse.builder()
                .totalResumes(totalResumes)
                .activeResumes(activeResumes)
                .profileCompletion(profileCompletion)
                .recentActivities(recentActivities)
                .build();
    }

    private int calculateProfileCompletion(User user) {
        int score = 0;
        int total = 10;

        if (user.getFirstName() != null && !user.getFirstName().isBlank()) score++;
        if (user.getLastName() != null && !user.getLastName().isBlank()) score++;
        if (user.getEmail() != null && !user.getEmail().isBlank()) score++;
        if (user.getPhone() != null && !user.getPhone().isBlank()) score++;
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isBlank()) score++;
        if (user.getHeadline() != null && !user.getHeadline().isBlank()) score++;
        if (user.getSummary() != null && !user.getSummary().isBlank()) score++;
        if (user.getLocation() != null && !user.getLocation().isBlank()) score++;
        if (user.getLinkedinUrl() != null && !user.getLinkedinUrl().isBlank()) score++;
        if (user.getGithubUrl() != null && !user.getGithubUrl().isBlank()) score++;

        return (score * 100) / total;
    }

    private DashboardResponse.ActivityItem mapToActivityItem(DashboardActivity activity) {
        return DashboardResponse.ActivityItem.builder()
                .id(activity.getId().toString())
                .activityType(activity.getActivityType())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .timestamp(activity.getCreatedAt() != null 
                        ? activity.getCreatedAt().toString() 
                        : null)
                .build();
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
