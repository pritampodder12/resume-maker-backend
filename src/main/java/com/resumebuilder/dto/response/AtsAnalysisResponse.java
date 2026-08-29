package com.resumebuilder.dto.response;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsAnalysisResponse {

    private UUID analysisId;
    private AtsScore atsScore;
    private List<KeywordMatch> extractedKeywords;

    @Data
    public static class AtsScore {
        private int overall;
        private int keyword;
        private int formatting;
        private int impact;
    }

    @Data
    public static class KeywordMatch {
        private String keyword;
        private boolean matched;
    }
}
