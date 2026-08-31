package com.resumebuilder.dto.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resume Section data")
public class SectionResponse {
    private UUID id;
    private String sectionType;
    private Integer sectionOrder;
    private String title;
    private String content;
    private boolean visible = true;
}
