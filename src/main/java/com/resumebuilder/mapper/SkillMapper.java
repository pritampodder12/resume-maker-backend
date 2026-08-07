package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.SkillsResponse;
import com.resumebuilder.entity.Skill;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillsResponse toResponse(Skill skill);
    List<SkillsResponse> toResponseList(List<Skill> skills);
}
