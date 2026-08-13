package com.resumebuilder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "education", indexes = {
        @Index(name = "idx_education_resume_id", columnList = "resume_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    @Column(name = "degree", nullable = false)
    private String degree;

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(name = "location")
    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private boolean current = false;

    @Column(name = "gpa")
    private String gpa;

    @JdbcTypeCode((SqlTypes.ARRAY))
    @Column(name = "description", columnDefinition = "TEXT[]")
    private List<String> description;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Education education)) return false;
        return id != null && id.equals(education.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
