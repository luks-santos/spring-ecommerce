CREATE TABLE processed_events (
    event_id     VARCHAR(36) NOT NULL,
    processed_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_processed_events PRIMARY KEY (event_id)
);
