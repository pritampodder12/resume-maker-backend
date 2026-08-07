package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.entity.Resume;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        CertificationMapper.class,
        EducationMapper.class,
        ExperienceMapper.class,
        ProjectMapper.class,
        SkillMapper.class,
        SectionMapper.class
})
public interface ResumeMapper {

//    @Mapping(target = "active", source = "active")
    ResumeResponse toResumeResponse(Resume resume);

    List<ResumeResponse> toResumeResponseList(List<Resume> resumes);
}