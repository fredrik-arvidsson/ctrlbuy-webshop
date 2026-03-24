-- OAuth2 social login support (Google, etc.)
ALTER TABLE users ADD COLUMN oauth_provider VARCHAR(50) DEFAULT NULL;
ALTER TABLE users ADD COLUMN oauth_provider_id VARCHAR(255) DEFAULT NULL;
ALTER TABLE users ADD COLUMN profile_picture_url VARCHAR(500) DEFAULT NULL;
