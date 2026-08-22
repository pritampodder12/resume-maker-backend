package com.resumebuilder.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
public class AtsAnalysisResponse {

    private AtsScore atsScore;
    private List<KeywordMatch> extractedKeywords;

    @Data
    private static class AtsScore {
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
