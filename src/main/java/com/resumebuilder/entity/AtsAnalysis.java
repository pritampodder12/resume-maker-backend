package com.resumebuilder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "ats_analysis", indexes = {
        @Index(name = "idx_ats_analysis_resume_id", columnList = "resume_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

//    @Lob
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "overall_score", nullable = false)
    private int overallScore;

    @Column(name = "keywords_score", nullable = false)
    private int keywordsScore;

    @Column(name = "formatting_score", nullable = false)
    private int formattingScore;

    @Column(name = "impact_score", nullable = false)
    private int impactScore;

    @Builder.Default
    @OneToMany(mappedBy = "atsAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AtsAnalysisKeyword> keywords = new HashSet<>();

//    public void addKeyword(AtsAnalysisKeyword keyword) {
//        keyword.setAtsAnalysis(this);
//        this.keywords.add(keyword);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtsAnalysis atsAnalysis)) return false;
        return id != null && id.equals(atsAnalysis.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
