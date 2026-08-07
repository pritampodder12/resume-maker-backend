package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.ProjectsResponse;
import com.resumebuilder.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectsResponse toResponse(Project project);
    List<ProjectsResponse> toResponseList(List<Project> projects);
}
