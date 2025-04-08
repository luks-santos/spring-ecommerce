CREATE TABLE IF NOT EXISTS inventories (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    quantity INT NOT NULL,
    product_id BINARY(16) UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
