package com.resumebuilder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard response with user statistics and activities")
public class DashboardResponse {

    @Schema(description = "Total number of resumes", example = "5")
    private long totalResumes;

    @Schema(description = "Number of active resumes", example = "2")
    private long activeResumes;

    @Schema(description = "Profile completion percentage", example = "85")
    private int profileCompletion;

    @Schema(description = "Recent activities")
    private List<ActivityItem> recentActivities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Activity item")
    public static class ActivityItem {

        @Schema(description = "Activity ID")
        private String id;

        @Schema(description = "Activity type", example = "RESUME_CREATED")
        private String activityType;

        @Schema(description = "Activity title", example = "Resume Created")
        private String title;

        @Schema(description = "Activity description")
        private String description;

        @Schema(description = "Activity timestamp")
        private String timestamp;
    }
}
