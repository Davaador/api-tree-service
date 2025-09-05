-- Create biography_history table
CREATE TABLE biography_history (
    id BIGSERIAL PRIMARY KEY,
    biography_content TEXT,
    version_number INTEGER,
    change_description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    biography_id BIGINT NOT NULL,
    deleted_at TIMESTAMP NULL,
    
    CONSTRAINT fk_biography_history_biography 
        FOREIGN KEY (biography_id) REFERENCES biography(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX fn_biography_history_biography ON biography_history(biography_id);
CREATE INDEX fn_biography_history_created ON biography_history(created_at);

-- Add version tracking to biography table
ALTER TABLE biography ADD COLUMN IF NOT EXISTS version_count INTEGER DEFAULT 0;
