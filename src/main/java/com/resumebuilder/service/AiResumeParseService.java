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
              "objective": "string",
              "templateName": "modern",
              "education": [{"degree": "string", "institutionName": "string", "fieldOfStudy": "string",
                             "location": "string", "startDate": "string", "endDate": "string",
                             "current": boolean, "gpa": "string", "description": ["string"], "sortOrder": number}],
              "experience": [{"companyName": "string", "position": "string", "location": "string",
                              "employmentType": "string", "startDate": "string", "endDate": "string",
                              "current": boolean, "description": ["string"], "highlights": "string", "sortOrder": number}],
              "projects": [{"name": "string", "description": ["string"], "technologies": "string",
                            "projectUrl": "string", "githubUrl": "string",
                            "startDate": "string", "endDate": "string",
                            "current": boolean, "sortOrder": number}],
              "skills": [{"name": "string", "category": "string", "proficiencyLevel": number 0-100,
                         "yearsOfExperience": number, "description": [], "sortOrder": number}],
              "certifications": [{"name": "string", "issuingOrganization": "string", "issueDate": "string",
                                  "expirationDate": "string", "doesNotExpire": boolean,
                                  "credentialId": "string", "credentialUrl": "string",
                                  "description": [], "sortOrder": number}]
            }
            
            CRITICAL RULE FOR MISSING DATA:
            If a field's value cannot be found in the resume text, DO NOT include that key in the JSON output at all.
            Do NOT write the word "omit", "null", "N/A", "", or any placeholder text as a value.
            Simply leave the key out of the JSON object entirely.
            
            Example — if expirationDate is not found, write:
            {"name": "AWS Certified", "issuingOrganization": "AWS", "issueDate": "2024-01-01"}
            NOT:
            {"name": "AWS Certified", "issuingOrganization": "AWS", "issueDate": "2024-01-01", "expirationDate": "omit"}
            
            RULE FOR DATE FIELDS (startDate, endDate, issueDate, expirationDate):
            These fields are plain strings, not strict dates, so follow this logic:
            
            1. If a real calendar date is given (e.g. "Jan 2023", "2023", "03/2023"):
               - Convert it to "YYYY-MM-DD" format
               - If a date has only a year, use YYYY-01-01
            
            2. If the resume text says the entry is ongoing, using any of these words or an obvious equivalent
               ("Present", "Current", "Ongoing", "Till Date", "Till Now", "Now", "Continuing"):
               - For endDate/expirationDate specifically: write the literal string "Present"
               - Set "current": true (for endDate only, not expirationDate)
            
            3. If the field is not mentioned at all in the resume (truly blank):
               - DO NOT include that key at all
            
            Worked examples:
            - Text: "Software Engineer, Jan 2022 - Present"
              -> {"startDate": "2022-01-01", "endDate": "Present", "current": true}
            - Text: "Analyst, 2019 - 2021"
              -> {"startDate": "2019-01-01", "endDate": "2021-01-01", "current": false}
            - Text: "Intern, Summer 2020" (no end date given at all)
              -> {"startDate": "2020-01-01", "current": false}   (no endDate key)
            - Text: "AWS Certified, issued Jan 2024, does not expire"
              -> {"issueDate": "2024-01-01", "doesNotExpire": true}   (no expirationDate key)
            
            Other rules:
            - "current" is true ONLY if the resume explicitly says "Present"/"Current"/"Ongoing"/"Till Date"/
              "Till Now"/"Now"/"Continuing" (or an obvious equivalent) for that entry's end date
            - When "current" is true, endDate MUST be the literal string "Present" — never leave it blank
              and never invent a real date
            - Preserve original wording for descriptions/bullet points, do not paraphrase
            - sortOrder starts at 1 and increments within each section
            """;

    public ResumeResponse parseResume(String rawText) {

        int key = rawText.hashCode();

        if(devCache.containsKey(key)) {
            log.info("Returning cached resume parse result (dev mode)");
            return devCache.get(key);
        }

//      String aiResponse = callGeminiApi(rawText);
        String aiResponse = callBedrockApi(rawText);
        ResumeResponse result = deserialize(aiResponse);
        devCache.put(key, result);

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

