package com.resumebuilder.service;

import com.resumebuilder.dto.request.CreateResumeRequest;
import com.resumebuilder.dto.request.UpdateResumeRequest;
import com.resumebuilder.dto.response.PagedResponse;
import com.resumebuilder.dto.response.ResumeResponse;

import java.util.UUID;

public interface ResumeService {

    ResumeResponse createResume(CreateResumeRequest request);

    ResumeResponse getResumeById(UUID id);

    PagedResponse<ResumeResponse> getResumes(int page, int size, String sortBy, String sortDir);

    ResumeResponse updateResume(UUID id, UpdateResumeRequest request);

    void deleteResume(UUID id);
}
