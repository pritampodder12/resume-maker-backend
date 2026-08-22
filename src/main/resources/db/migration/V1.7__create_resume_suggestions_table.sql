-- ============================================================
-- V102__create_resume_suggestions_table.sql
-- Rename version prefix to match your actual next Flyway version.
-- ============================================================

CREATE TABLE resume_suggestions (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id          UUID NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    analysis_id        UUID REFERENCES ats_analysis(id) ON DELETE CASCADE,
    section            VARCHAR(50) NOT NULL,   -- EXPERIENCE | EDUCATION | SKILLS | PROJECTS ...
    type               VARCHAR(20) NOT NULL,   -- KEYWORD | REWRITE | METRIC
    entry_index        INTEGER,                -- index into the section array on the resume
    bullet_index       INTEGER,                -- index into that entry's description[]; null = new bullet
    title              VARCHAR(255) NOT NULL,
    description        TEXT,
    current_text       TEXT,
    suggested_text     TEXT NOT NULL,
    status             VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING | APPLIED | DISMISSED
    applied_at         TIMESTAMP,
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

CREATE INDEX idx_resume_suggestions_resume_id ON resume_suggestions(resume_id);
CREATE INDEX idx_resume_suggestions_analysis_id ON resume_suggestions(analysis_id);
CREATE INDEX idx_resume_suggestions_status ON resume_suggestions(status);