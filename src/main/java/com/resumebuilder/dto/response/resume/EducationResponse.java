package com.resumebuilder.dto.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Education data")
public class EducationResponse {
    private UUID id;
    private String institutionName;
    private String degree;
    private String fieldOfStudy;
    private String location;
    private String startDate;
    private String endDate;
    private boolean current;
    private String gpa;
    private List<String> description;

}
