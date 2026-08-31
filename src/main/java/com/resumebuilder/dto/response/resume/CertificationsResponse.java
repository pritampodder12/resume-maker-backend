package com.resumebuilder.dto.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Certification data")
public class CertificationsResponse {
    private UUID id;
    private String name;
    private String issuingOrganization;
    private String credentialId;
    private String credentialUrl;
    private String issueDate;
    private String expirationDate;
    private boolean doesNotExpire = false;
    private List<String> description;
    private Integer sortOrder = 0;
}
