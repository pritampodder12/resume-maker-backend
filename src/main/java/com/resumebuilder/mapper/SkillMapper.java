package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.SkillsResponse;
import com.resumebuilder.entity.Skill;
import org.mapstruct.Mapper;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillsResponse toResponse(Skill skill);
    List<SkillsResponse> toResponseList(List<Skill> skills);

    default Map<String, List<SkillsResponse>> toGroupResponseList(Set<Skill> skills) {
        if(skills == null || skills.isEmpty()) {
            return Map.of();
        }
        return skills.stream()
                .sorted(Comparator.comparing(Skill::getCategory, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Skill::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(
                        Skill::getCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toResponse, Collectors.toList())
                ));
    }
}
