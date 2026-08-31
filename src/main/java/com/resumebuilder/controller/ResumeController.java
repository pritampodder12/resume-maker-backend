package com.resumebuilder.controller;

import com.resumebuilder.constant.AppConstants;
import com.resumebuilder.dto.request.AtsAnalysisRequest;
import com.resumebuilder.dto.request.CreateResumeRequest;
import com.resumebuilder.dto.request.SuggestionRequest;
import com.resumebuilder.dto.request.UpdateResumeRequest;
import com.resumebuilder.dto.response.*;
import com.resumebuilder.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Endpoints for managing resumes")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    @Operation(summary = "Create a new resume", description = "Creates a new resume for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Resume created successfully")
    public ResponseEntity<ApiResponse<ResumeResponse>> createResume(@Valid @RequestBody CreateResumeRequest request) {
        ResumeResponse response = resumeService.createResume(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resume by ID", description = "Retrieves a specific resume by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume retrieved successfully")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResumeById(
            @Parameter(description = "Resume ID") @PathVariable UUID id) {
        ResumeResponse response = resumeService.getResumeById(id);
        return ResponseEntity.ok(ApiResponse.success("Resume retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all resumes", description = "Retrieves paginated list of resumes for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resumes retrieved successfully")
    public ResponseEntity<ApiResponse<PagedResponse<ResumeResponse>>> getResumes(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        PagedResponse<ResumeResponse> response = resumeService.getResumes(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Resumes retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update resume", description = "Updates an existing resume")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume updated successfully")
    public ResponseEntity<ApiResponse<ResumeResponse>> updateResume(
            @Parameter(description = "Resume ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateResumeRequest request) {
        ResumeResponse response = resumeService.updateResume(id, request);
        return ResponseEntity.ok(ApiResponse.success("Resume updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume", description = "Soft deletes a resume")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume deleted successfully")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @Parameter(description = "Resume ID") @PathVariable UUID id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully"));
    }

    @PostMapping(value = "/parse-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Parse pdf file", description = "Parse pdf resume into json")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume parsed successfully")
    public ResponseEntity<ApiResponse<ResumeResponse>> createResumeFromPdf(
            @Parameter(description = "Resume pdf file") @RequestParam("file") MultipartFile file
    ) {
        ResumeResponse response = resumeService.createResumeFromPdf(file);
        return ResponseEntity.ok(ApiResponse.success("Resume parsed successfully", response));
    }

    @PostMapping("/{id}/ats-analysis")
    @Operation(summary = "Generate Resume ATS score", description = "ATS score correspond to a job description")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume Analysis successful")
    public ResponseEntity<ApiResponse<AtsAnalysisResponse>> atsAnalysisFromJD(
            @Valid @RequestBody AtsAnalysisRequest request,
            @Parameter(description = "Resume ID") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("ATS analysis successful", resumeService.analyseResume(id, request.getJobDescription())));
    }

    @PostMapping("/{resumeId}/{analysisId}/suggestions")
    @Operation(summary = "Get AI suggestions for a resume section",
            description = "Generates KEYWORD/REWRITE/METRIC suggestions for one section, based on a prior ATS analysis")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suggestions generated successfully")
    public ResponseEntity<ApiResponse<SuggestionsResponse>> generateSuggestion(
            @Parameter(description = "Resume ID") @PathVariable UUID resumeId,
            @Parameter(description = "Analysis ID") @PathVariable UUID analysisId,
            @Valid @RequestBody SuggestionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Suggestions generated successful", resumeService.generateSuggestion(resumeId, analysisId, request.getSection())));
    }

}
