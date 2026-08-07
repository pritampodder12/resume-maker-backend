package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.EducationResponse;
import com.resumebuilder.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    @Mapping(target="institutionName", source = "institutionName")
    @Mapping(target="degree", source = "degree")
    EducationResponse toResponse(Education education);

    List<EducationResponse> toResponseList(List<Education> educations);
}
