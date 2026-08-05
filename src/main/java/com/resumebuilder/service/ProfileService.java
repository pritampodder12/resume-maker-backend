package com.resumebuilder.service;

import com.resumebuilder.dto.request.UpdateProfileRequest;
import com.resumebuilder.dto.response.UserResponse;

public interface ProfileService {

    UserResponse getProfile();

    UserResponse updateProfile(UpdateProfileRequest request);
}
