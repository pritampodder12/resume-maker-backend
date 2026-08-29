package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.AtsAnalysisResponse;
import com.resumebuilder.entity.AtsAnalysis;
import com.resumebuilder.entity.AtsAnalysisKeyword;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AtsAnalysisKeywordMapper {
    AtsAnalysisResponse.KeywordMatch toKeywordMatch(AtsAnalysisKeyword entity);

    List<AtsAnalysisResponse.KeywordMatch> toKeywordMatchList(Set<AtsAnalysisKeyword> entities);
}
