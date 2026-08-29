package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.AtsAnalysisResponse;
import com.resumebuilder.entity.AtsAnalysis;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        AtsAnalysisKeywordMapper.class
})
public interface AtsAnalysisMapper {
    default AtsAnalysisResponse toAtsAnalysisResponse(AtsAnalysis entity) {
        if (entity == null) {
            return null;
        }

        AtsAnalysisResponse.AtsScore score = new AtsAnalysisResponse.AtsScore();
        score.setOverall(entity.getOverallScore());
        score.setKeyword(entity.getKeywordsScore());
        score.setFormatting(entity.getFormattingScore());
        score.setImpact(entity.getImpactScore());

        List<AtsAnalysisResponse.KeywordMatch> keywords = entity.getKeywords().stream()
                .map(k -> {
                    AtsAnalysisResponse.KeywordMatch km = new AtsAnalysisResponse.KeywordMatch();
                    km.setKeyword(k.getKeyword());
                    km.setMatched(k.isMatched());
                    return km;
                })
                .toList();

        return AtsAnalysisResponse.builder()
                .analysisId(entity.getId())
                .atsScore(score)
                .extractedKeywords(keywords)
                .build();
    }


}
