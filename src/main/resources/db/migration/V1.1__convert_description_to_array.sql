-- education
ALTER TABLE education
    ALTER COLUMN description TYPE TEXT[]
    USING CASE WHEN description IS NULL THEN NULL ELSE ARRAY[description] END;

-- experience
ALTER TABLE experience
    ALTER COLUMN description TYPE TEXT[]
    USING CASE WHEN description IS NULL THEN NULL ELSE ARRAY[description] END;

-- projects
ALTER TABLE projects
    ALTER COLUMN description TYPE TEXT[]
    USING CASE WHEN description IS NULL THEN NULL ELSE ARRAY[description] END;

-- skills
ALTER TABLE skills
    ALTER COLUMN description TYPE TEXT[]
    USING CASE WHEN description IS NULL THEN NULL ELSE ARRAY[description] END;

-- certifications
ALTER TABLE certifications
    ALTER COLUMN description TYPE TEXT[]
    USING CASE WHEN description IS NULL THEN NULL ELSE ARRAY[description] END;
