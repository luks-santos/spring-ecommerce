CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    refresh_token VARCHAR(10000) NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
