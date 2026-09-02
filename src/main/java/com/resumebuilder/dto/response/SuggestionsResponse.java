package com.resumebuilder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SuggestionsResponse {

    private List<Suggestion> suggestions;

    public void resolveEntryIds(List<UUID> entryIds) {
        if (suggestions == null) return;
        suggestions.forEach(suggestion -> suggestion.resolveEntryId(entryIds, log));
    }


    @Data
    private static class Suggestion {
        private String type;
        private String section;
        private TargetRef targetRef;
        private String title;
        private String description;
        private String currentText;
        private String suggestedText;

        private void resolveEntryId(List<UUID> entryIds, org.slf4j.Logger log) {
            Integer entryIndex = targetRef.getEntryIndex();
            if (entryIndex != null && entryIndex >= 0 && entryIndex < entryIds.size()) {
                targetRef.setEntryId(entryIds.get(entryIndex));
            } else {
                log.warn("AI returned out-of-range entryIndex {} (list size {}) — suggestion '{}' will have no entryId",
                        entryIndex, entryIds.size(), title);
            }
            targetRef.setEntryIndex(null);
        }
    }

    @Data
    private static class TargetRef {
        private Integer entryIndex;
        private UUID entryId;
        private Integer bulletIndex;
        private String category;
    }

}
