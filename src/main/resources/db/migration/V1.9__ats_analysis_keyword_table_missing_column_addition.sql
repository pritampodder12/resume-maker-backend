ALTER TABLE ats_analysis_keywords
    ADD COLUMN updated_by VARCHAR(255),
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;