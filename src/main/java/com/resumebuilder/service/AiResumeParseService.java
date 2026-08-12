package com.resumebuilder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiResumeParseService {
    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are a resume parser. Given raw resume text, extract the information and
            return ONLY valid JSON (no markdown fences, no explanation) matching this exact structure:

            {
              "title": "string (required, e.g. 'Software Engineer Resume', Only position/designation)",
              "candidateName": "string — the full name of the person this resume belongs to, e.g. 'Pritam Podder'",
              "objective": "string or omit",
              "templateName": "modern",
              "education": [{"degree": "string", "institutionName": "string", "fieldOfStudy": "string",
                             "location": "string", "startDate": "YYYY-MM-DD or omit", "endDate": "YYYY-MM-DD or omit",
                             "current": boolean, "gpa": "string or omit", "description": ["string"], "sortOrder": number}],
              "experience": [{"companyName": "string", "position": "string", "location": "string",
                              "employmentType": "string", "startDate": "YYYY-MM-DD", "endDate": "YYYY-MM-DD or omit",
                              "current": boolean, "description": ["string"], "highlights": "string", "sortOrder": number}],
              "projects": [{"name": "string", "description": ["string"], "technologies": "string",
                            "projectUrl": "string or omit", "githubUrl": "string or omit",
                            "startDate": "YYYY-MM-DD or omit", "endDate": "YYYY-MM-DD or omit",
                            "current": boolean, "sortOrder": number}],
              "skills": [{"name": "string", "category": "string", "proficiencyLevel": number 0-100,
                         "yearsOfExperience": number, "description": [], "sortOrder": number}],
              "certifications": [{"name": "string", "issuingOrganization": "string", "issueDate": "YYYY-MM-DD",
                                  "expirationDate": "YYYY-MM-DD or omit", "doesNotExpire": boolean,
                                  "credentialId": "string or omit", "credentialUrl": "string or omit",
                                  "description": [], "sortOrder": number}]
            }

            Rules:
            - If a date has only a year, use YYYY-01-01
            - "current" is true only if the resume explicitly says "Present" or equivalent
            - Omit fields you cannot find rather than guessing or inventing data
            - Preserve original wording for descriptions/bullet points, do not paraphrase
            - sortOrder starts at 1 and increments within each section
            """;

    public ResumeResponse parseResume(String rawText) {
        String aiResponse = callGeminiApi(rawText);

        return deserialize(aiResponse);
    }

    @SuppressWarnings("unchecked")
    private String callGeminiApi(String rawText) {

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                ),
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", rawText)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "responseMimeType", "application/json"
                )
        );

        try {
            Map<String, Object> response = geminiWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");

            if(candidates == null || candidates.isEmpty()) {
                throw new BadRequestException("AI did not return any content. Please try again.");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new BadRequestException("Failed to parse resume using AI. Please try again.");
        }
    }

    private ResumeResponse deserialize(String aiResponse) {
        String cleaned = aiResponse.replaceAll("```json|```", "").trim();
        try {
            return objectMapper.readValue(cleaned, ResumeResponse.class);
        } catch (Exception e) {
            log.error("Failed to deserialize AI response into CreateResumeRequest. Raw response: {}", cleaned, e);
            throw new BadRequestException("AI returned an unexpected format. Please try again or enter details manually.");
        }
    }
}

