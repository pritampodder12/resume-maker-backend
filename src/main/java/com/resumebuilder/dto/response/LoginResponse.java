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
@Schema(description = "Response after successful login")
public class LoginResponse {

    @Schema(description = "Access token for API authentication", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh token for getting new access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration in milliseconds", example = "900000")
    private Long expiresIn;

    @Schema(description = "Basic user information")
    private UserBasicInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic user information")
    public static class UserBasicInfo {
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

        @Schema(description = "User role", example = "USER")
        private String role;
    }
}
