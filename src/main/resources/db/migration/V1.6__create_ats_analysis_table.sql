-- ============================================================
-- V101__create_ats_analysis_tables.sql
-- Rename version prefix to match your actual next Flyway version.
-- ============================================================

CREATE TABLE ats_analysis (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id          UUID NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    job_description    TEXT NOT NULL,
    overall_score      INTEGER NOT NULL,
    keywords_score     INTEGER NOT NULL,
    formatting_score   INTEGER NOT NULL,
    impact_score       INTEGER NOT NULL,
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

CREATE INDEX idx_ats_analysis_resume_id ON ats_analysis(resume_id);

CREATE TABLE ats_analysis_keywords (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id    UUID NOT NULL REFERENCES ats_analysis(id) ON DELETE CASCADE,
    keyword        VARCHAR(255) NOT NULL,
    matched        BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order     INTEGER,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ats_analysis_keywords_analysis_id ON ats_analysis_keywords(analysis_id);