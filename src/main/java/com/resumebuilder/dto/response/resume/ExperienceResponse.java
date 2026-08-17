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
@Schema(description = "Experience data")
public class ExperienceResponse {
    private String companyName;
    private String position;
    private String location;
    private String employmentType;
    private String startDate;
    private String endDate;
    private boolean current;
    private List<String> description;
    private String highlights;
    private Integer sortOrder;
}
