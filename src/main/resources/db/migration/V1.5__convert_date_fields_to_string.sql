-- Experience table
ALTER TABLE experience
    ALTER COLUMN start_date TYPE VARCHAR(20) USING start_date::VARCHAR,
    ALTER COLUMN end_date TYPE VARCHAR(20) USING end_date::VARCHAR;

-- Education table
ALTER TABLE education
    ALTER COLUMN start_date TYPE VARCHAR(20) USING start_date::VARCHAR,
    ALTER COLUMN end_date TYPE VARCHAR(20) USING end_date::VARCHAR;

-- Projects table
ALTER TABLE projects
    ALTER COLUMN start_date TYPE VARCHAR(20) USING start_date::VARCHAR,
    ALTER COLUMN end_date TYPE VARCHAR(20) USING end_date::VARCHAR;

-- Certifications table (note: issue_date / expiration_date, not start/end)
ALTER TABLE certifications
    ALTER COLUMN issue_date TYPE VARCHAR(20) USING issue_date::VARCHAR,
    ALTER COLUMN expiration_date TYPE VARCHAR(20) USING expiration_date::VARCHAR;