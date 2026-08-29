package com.resumebuilder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ats_analysis_keywords", indexes = {
        @Index(name = "idx_ats_analysis_keywords_analysis_id", columnList = "analysis_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsAnalysisKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AtsAnalysis atsAnalysis;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private boolean matched;

    @Column(name = "sort_order")
    private int sortOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtsAnalysisKeyword atsAnalysisKeyword)) return false;
        return id != null && id.equals(atsAnalysisKeyword.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
