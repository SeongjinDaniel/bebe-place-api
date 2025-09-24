-- Simple product creation tracking table (replaces complex Saga tables)
CREATE TABLE product_creation_logs (
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    correlation_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    current_step VARCHAR(50),
    failure_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key constraint
    CONSTRAINT fk_product_creation_logs_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Indexes for performance
CREATE INDEX idx_product_creation_logs_product_id ON product_creation_logs(product_id);
CREATE INDEX idx_product_creation_logs_correlation_id ON product_creation_logs(correlation_id);
CREATE INDEX idx_product_creation_logs_status ON product_creation_logs(status);
CREATE INDEX idx_product_creation_logs_created_at ON product_creation_logs(created_at DESC);