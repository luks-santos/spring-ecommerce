CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID()))
    refresh_token VARCHAR(10000) NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
