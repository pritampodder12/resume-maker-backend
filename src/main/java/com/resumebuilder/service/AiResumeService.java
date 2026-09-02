package com.resumebuilder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebuilder.constant.AiPrompts;
import com.resumebuilder.dto.response.AtsAnalysisResponse;
import com.resumebuilder.dto.response.ResumeResponse;
import com.resumebuilder.dto.response.SuggestionsResponse;
import com.resumebuilder.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiResumeService {
    private final WebClient geminiWebClient;
    public final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;

    private static final Path CACHE_DIR = Paths.get("dev-cache/resume-parse");
    private static final Path ATS_CACHE_DIR = Paths.get("dev-cache/ats-analysis");
    private static final Path SUGGESTIONS_CACHE_DIR = Paths.get("dev-cache/suggestions");

    @Value("${app.bedrock.model-id}")
    private String modelId;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    public ResumeResponse parseResume(String rawText) {

        int key = rawText.hashCode();

        ResumeResponse cached = readFromDiskCache(CACHE_DIR, key, ResumeResponse.class);
        if(cached != null) {
            log.info("Returning cached resume parse result (dev mode, disk)");
            return cached;
        }

        String aiResponse = callBedrockApi(AiPrompts.PARSE_RESUME_PROMPT, rawText);
        ResumeResponse result = deserialize(aiResponse, ResumeResponse.class);
        writeToDisk(CACHE_DIR, key, result);

        return result;
    }

    public AtsAnalysisResponse analyseJobMatch(ResumeResponse resume, String jobDescription) {
        String userContent = buildAnalysisUserContent(resume, jobDescription);
        int key = userContent.hashCode();

        AtsAnalysisResponse cached = readFromDiskCache(ATS_CACHE_DIR, key, AtsAnalysisResponse.class);
        if(cached != null) {
            log.info("Returning cached ATS analysis parse result (dev mode, disk)");
            return cached;
        }

        String aiResponse = callBedrockApi(AiPrompts.ATS_ANALYSIS_SYSTEM_PROMPT, userContent);
        AtsAnalysisResponse result = deserialize(aiResponse, AtsAnalysisResponse.class);
        writeToDisk(ATS_CACHE_DIR, key, result);

        return result;
    }

    private String buildAnalysisUserContent(ResumeResponse resume, String jobDescription) {
        try {
            return """
                    RESUME:
                    %s
                    
                    JOB DESCRIPTION:
                    %S
                    """.formatted(objectMapper.writeValueAsString(resume), jobDescription);
        } catch (Exception e) {
            log.error("Failed to serialize resume for ATS analysis", e);
            throw new BadRequestException("Could not process resume for analysis.");
        }
    }

    public SuggestionsResponse generateSuggestions(Object sectionData,
                                                   String section,
                                                   String jobDescription,
                                                   List<String> missingKeywords) {
        String userContent = buildSuggestionsUserContent(sectionData, section, jobDescription, missingKeywords);
        int key = userContent.hashCode();

        SuggestionsResponse cached = readFromDiskCache(SUGGESTIONS_CACHE_DIR, key, SuggestionsResponse.class);
        if (cached != null) {
            log.info("Returning cached suggestions result (dev mode, disk)");
            return cached;
        }

        String systemPrompt = switch (section.toUpperCase()) {
            case "EXPERIENCE", "PROJECTS", "EDUCATION", "CERTIFICATIONS" -> AiPrompts.SUGGESTIONS_SYSTEM_PROMPT;
            case "SKILLS" -> AiPrompts.SKILLS_SUGGESTIONS_SYSTEM_PROMPT;
            case "SUMMARY" -> AiPrompts.SUMMARY_SUGGESTIONS_SYSTEM_PROMPT;
            default -> throw new BadRequestException("Unsupported section: " + section);
        };

        String aiResponse = callBedrockApi(systemPrompt, userContent);
        SuggestionsResponse result = deserialize(aiResponse, SuggestionsResponse.class);
        writeToDisk(SUGGESTIONS_CACHE_DIR, key, result);

        return result;
    }

    private String buildSuggestionsUserContent(Object sectionData,
                                               String section,
                                               String jobDescription,
                                               List<String> missingKeywords) {
        try {
            // For SKILLS, strip id/sortOrder/proficiency/etc — send only category -> [name, name, ...]
            // so there is no numeric field the model can mistake for an index.
            Object payload = "SKILLS".equalsIgnoreCase(section)
                    ? groupSkillsByCategory(sectionData)
                    : sectionData;

            return """
                RESUME SECTION (%s):
                %s

                JOB DESCRIPTION:
                %s

                MISSING KEYWORDS:
                %s
                """.formatted(
                    section,
                    objectMapper.writeValueAsString(payload),
                    jobDescription,
                    objectMapper.writeValueAsString(missingKeywords)
            );
        } catch (Exception e) {
            log.error("Failed to serialize section data for suggestions", e);
            throw new BadRequestException("Could not process resume section for suggestions.");
        }
    }

    private String callBedrockApi(String systemPrompt, String userContent) {
        try {
            // ADD THIS — logs exactly what's about to be sent
            log.info("===== BEDROCK SYSTEM PROMPT =====\n{}", systemPrompt);
            log.info("===== BEDROCK USER CONTENT =====\n{}", userContent);
            ConverseRequest request = ConverseRequest.builder()
                    .modelId(modelId)
                    .system(SystemContentBlock.builder().text(systemPrompt).build())
                    .messages(Message.builder()
                            .role(ConversationRole.USER)
                            .content(ContentBlock.fromText(userContent))
                            .build())
                    .inferenceConfig(InferenceConfiguration.builder()
                            .temperature(0.2f)
                            .maxTokens(4096)
                            .build())
                    .build();

            ConverseResponse response = bedrockRuntimeClient.converse(request);
            String rawResponse = response.output().message().content().get(0).text();
            log.info("===== BEDROCK RAW RESPONSE =====\n{}", rawResponse);
            return rawResponse;
        } catch (BedrockRuntimeException e) {
            log.error("Bedrock API call failed", e);
            throw new BadRequestException("Failed to process request using AI. Please try again.");
        }
    }

    @SuppressWarnings("unchecked")
    private String callGeminiApi(String rawText) {

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", AiPrompts.PARSE_RESUME_PROMPT))
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

    private <T> T deserialize(String aiResponse, Class<T> clazz) {
        String cleaned = aiResponse.replaceAll("```json|```", "").trim();
        try {
            return objectMapper.readValue(cleaned, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize AI response into CreateResumeRequest. Raw response: {}", clazz.getSimpleName(), e);
            throw new BadRequestException("AI returned an unexpected format. Please try again or enter details manually.");
        }
    }

    private <T> T readFromDiskCache(Path cacheDir, int key, Class<T> clazz) {
        try {
            Path file = cacheDir.resolve(key + ".json");
            if(!Files.exists(file)) return null;
            return objectMapper.readValue(file.toFile(), clazz);
        } catch (Exception e) {
            log.warn("Failed to read dev cache file for key {}", key, e);
            return null;
        }
    }

    private void writeToDisk(Path cacheDir, int key, Object result) {
        try {
            Files.createDirectories(cacheDir);
            Path file = cacheDir.resolve(key + ".json");
            objectMapper.writeValue(file.toFile(), result);
        } catch (Exception e) {
            log.warn("Failed to write dev cache file for key {}", key, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> groupSkillsByCategory(Object sectionData) {
        // sectionData arrives as a generic Object (List<Skill> or already-deserialized List<Map>),
        // so normalize through the ObjectMapper rather than casting directly.
        List<Map<String, Object>> skills = objectMapper.convertValue(
                sectionData, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
        );

        return skills.stream().collect(java.util.stream.Collectors.groupingBy(
                s -> String.valueOf(s.get("category")),
                java.util.LinkedHashMap::new,
                java.util.stream.Collectors.mapping(s -> String.valueOf(s.get("name")), java.util.stream.Collectors.toList())
        ));
    }
}

