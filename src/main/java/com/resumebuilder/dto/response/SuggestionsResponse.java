package com.resumebuilder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionsResponse {

    private List<Suggestion> suggestions;

    @Data
    private static class Suggestion {
        private String type;
        private String section;
        private TargetRef targetRef;
        private String title;
        private String description;
        private String currentText;
        private String suggestedText;
    }

    @Data
    private static class TargetRef {
        private Integer entryIndex;
        private Integer bulletIndex;
    }

}
