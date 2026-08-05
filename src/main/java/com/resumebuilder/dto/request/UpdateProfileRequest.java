package com.resumebuilder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating user profile")
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Schema(description = "User phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "URL to profile picture", example = "https://example.com/avatar.jpg")
    private String profilePictureUrl;

    @Size(max = 255, message = "Headline must not exceed 255 characters")
    @Schema(description = "Professional headline", example = "Senior Software Engineer")
    private String headline;

    @Schema(description = "Professional summary")
    private String summary;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    @Schema(description = "Location", example = "San Francisco, CA")
    private String location;

    @Schema(description = "Personal website URL")
    private String websiteUrl;

    @Schema(description = "LinkedIn profile URL")
    private String linkedinUrl;

    @Schema(description = "GitHub profile URL")
    private String githubUrl;
}
