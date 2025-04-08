CREATE TABLE IF NOT EXISTS products (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category_id BINARY(16),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
