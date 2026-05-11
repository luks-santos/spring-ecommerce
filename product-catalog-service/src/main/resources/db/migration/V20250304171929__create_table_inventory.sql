CREATE TABLE IF NOT EXISTS inventories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quantity INT NOT NULL,
    product_id UUID UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
