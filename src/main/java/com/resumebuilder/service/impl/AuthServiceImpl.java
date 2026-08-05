package com.resumebuilder.service.impl;

import com.resumebuilder.dto.request.LoginRequest;
import com.resumebuilder.dto.request.RefreshTokenRequest;
import com.resumebuilder.dto.request.RegisterRequest;
import com.resumebuilder.dto.response.LoginResponse;
import com.resumebuilder.dto.response.UserResponse;
import com.resumebuilder.entity.User;
import com.resumebuilder.enums.Role;
import com.resumebuilder.exception.DuplicateResourceException;
import com.resumebuilder.exception.UnauthorizedException;
import com.resumebuilder.mapper.UserMapper;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.security.CustomUserDetails;
import com.resumebuilder.security.JwtService;
import com.resumebuilder.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.USER)
                .enabled(true)
                .verified(false)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return generateLoginResponse(userDetails);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("User logged in successfully: {}", userDetails.getEmail());
        return generateLoginResponse(userDetails);
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Refreshing access token");

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            log.warn("Token is not a refresh token");
            throw new UnauthorizedException("Invalid token type");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.isEnabled()) {
            log.warn("User account is disabled: {}", email);
            throw new UnauthorizedException("Account is disabled");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return generateLoginResponse(userDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UUID userId = getCurrentUserId();
        log.debug("Fetching current user with ID: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return userMapper.toUserResponse(user);
    }

    private LoginResponse generateLoginResponse(CustomUserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(LoginResponse.UserBasicInfo.builder()
                        .id(userDetails.getId().toString())
                        .email(userDetails.getEmail())
                        .firstName(userDetails.getFirstName())
                        .lastName(userDetails.getLastName())
                        .fullName(userDetails.getFullName())
                        .role(userDetails.getRole().name())
                        .build())
                .build();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}

