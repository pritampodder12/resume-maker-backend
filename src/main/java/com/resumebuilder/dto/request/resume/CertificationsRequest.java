package com.resumebuilder.dto.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Certification entry for resume creation")
public class CertificationsRequest {

    @NotBlank(message = "Certification name is required")
    @Schema(description = "Certification name", example = "AWS Certified Solutions Architect", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Issuing organization is required")
    @Schema(description = "Issuing organization", example = "Amazon Web Services", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuingOrganization;

    @Schema(description = "Credential ID (optional)", example = "CERT-123456")
    private String credentialId;

    @Schema(description = "Credential URL (optional)", example = "https://www.credly.com/badges/123456")
    private String credentialUrl;

    @Schema(description = "Date when certification was issued", example = "2024-01-15")
    private LocalDate issueDate;

    @Schema(description = "Expiration date (omitted if doesNotExpire is true)", example = "2027-01-15")
    private LocalDate expirationDate;

    @Schema(description = "Does this certification expire?", example = "false")
    private boolean doesNotExpire;

    @Schema(description = "Description / additional details (multi-line)")
    private List<String> description;

    @Schema(description = "Sort order (lower numbers appear first)", example = "1")
    private Integer sortOrder;
}
