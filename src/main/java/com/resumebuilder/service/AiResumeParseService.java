package com.resumebuilder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiResumeParseService {
    private final WebClient geminiWebClient;
    public final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;
    private final Map<Integer, ResumeResponse> devCache = new ConcurrentHashMap<>();

    private static final Path CACHE_DIR = Paths.get("dev-cache/resume-parse");

    @Value("${app.bedrock.model-id}")
    private String modelId;

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
            """;

    public ResumeResponse parseResume(String rawText) {

        int key = rawText.hashCode();

//        if (devCache.containsKey(key)) {
//            log.info("Returning cached resume parse result (dev mode)");
//            return devCache.get(key);
//        }
//      String aiResponse = callGeminiApi(rawText);

        ResumeResponse cached = readFromDiskCache(key);
        if(cached != null) {
            log.info("Returning cached resume parse result (dev mode, disk)");
            return cached;
        }


        String aiResponse = callBedrockApi(rawText);
        ResumeResponse result = deserialize(aiResponse);
//        devCache.put(key, result);
        writeToDisk(key, result);

        return result;
    }

    private String callBedrockApi(String rawText) {
        try {
            ConverseRequest request = ConverseRequest.builder()
                    .modelId(modelId)
                    .system(SystemContentBlock.builder().text(SYSTEM_PROMPT).build())
                    .messages(Message.builder()
                            .role(ConversationRole.USER)
                            .content(ContentBlock.fromText(rawText))
                            .build())
                    .inferenceConfig(InferenceConfiguration.builder()
                            .temperature(0.2f)
                            .maxTokens(4096)
                            .build())
                    .build();

            ConverseResponse response = bedrockRuntimeClient.converse(request);

            return response.output().message().content().get(0).text();
        } catch (BedrockRuntimeException e) {
            log.error("Bedrock API call failed", e);
            throw new BadRequestException("Failed to parse resume using AI. Please try again.");
        }
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

            if (candidates == null || candidates.isEmpty()) {
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

    private ResumeResponse readFromDiskCache(int key) {
        try {
            Path file = CACHE_DIR.resolve(key + ".json");
            if(!Files.exists(file)) return null;
            return objectMapper.readValue(file.toFile(), ResumeResponse.class);
        } catch (Exception e) {
            log.warn("Failed to read dev cache file for key {}", key, e);
            return null;
        }
    }

    private void writeToDisk(int key, ResumeResponse result) {
        try {
            Files.createDirectories(CACHE_DIR);
            Path file = CACHE_DIR.resolve(key + ".json");
            objectMapper.writeValue(file.toFile(), result);
        } catch (Exception e) {
            log.warn("Failed to write dev cache file for key {}", key, e);
        }
    }
}

