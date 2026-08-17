package com.resumebuilder.dto.response.resume;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Projects data")
public class ProjectsResponse {
    private String name;
    private List<String> description;
    private String technologies;
    private String projectUrl;
    private String githubUrl;
    private String startDate;
    private String endDate;
    private boolean current;
    private Integer sortOrder;
}
