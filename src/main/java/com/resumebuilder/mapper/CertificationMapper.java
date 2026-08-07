package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.resume.CertificationsResponse;
import com.resumebuilder.entity.Certification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    CertificationsResponse toResponse(Certification certification);
    List<CertificationsResponse> toResponseList(List<Certification> certifications);
}
