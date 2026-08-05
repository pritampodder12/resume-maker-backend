package com.resumebuilder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response")
public class UserResponse {

    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Schema(description = "User full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "URL to profile picture", example = "https://example.com/avatar.jpg")
    private String profilePictureUrl;

    @Schema(description = "Professional headline", example = "Senior Software Engineer")
    private String headline;

    @Schema(description = "Professional summary")
    private String summary;

    @Schema(description = "Location", example = "San Francisco, CA")
    private String location;

    @Schema(description = "Personal website URL")
    private String websiteUrl;

    @Schema(description = "LinkedIn profile URL")
    private String linkedinUrl;

    @Schema(description = "GitHub profile URL")
    private String githubUrl;

    @Schema(description = "User role", example = "USER")
    private String role;

    @Schema(description = "Whether user is verified", example = "true")
    private Boolean verified;

    @Schema(description = "Account creation date")
    private String createdAt;
}
