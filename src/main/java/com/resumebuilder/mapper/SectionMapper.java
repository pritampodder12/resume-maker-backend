package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.SectionResponse;
import com.resumebuilder.entity.ResumeSection;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    SectionResponse toResponse(ResumeSection resumeSection);
    List<SectionResponse> toResponseList(List<ResumeSection> resumeSections);
}
