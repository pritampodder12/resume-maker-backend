package com.resumebuilder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "certifications", indexes = {
        @Index(name = "idx_certification_resume_id", columnList = "resume_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "issuing_organization", nullable = false)
    private String issuingOrganization;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "credential_url")
    private String credentialUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "does_not_expire", nullable = false)
    @Builder.Default
    private boolean doesNotExpire = false;

    @JdbcTypeCode((SqlTypes.ARRAY))
    @Column(name = "description", columnDefinition = "TEXT[]")
    private List<String> description;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certification certification)) return false;
        return id != null && id.equals(certification.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}