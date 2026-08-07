package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.ExperienceResponse;
import com.resumebuilder.entity.Experience;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {
    ExperienceResponse toResponse(Experience experience);
    List<ExperienceResponse> toResponseList(List<Experience> experiences);
}
