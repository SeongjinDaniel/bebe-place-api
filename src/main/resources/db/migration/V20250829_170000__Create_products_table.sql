-- Products table creation
CREATE TABLE products (
    id UUID PRIMARY KEY,
    seller_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    shipping_included BOOLEAN NOT NULL DEFAULT false,
    shipping_cost DECIMAL(8,2),
    description TEXT NOT NULL,
    product_type VARCHAR(10) NOT NULL CHECK (product_type IN ('NEW', 'USED')),
    condition VARCHAR(10) NOT NULL CHECK (condition IN ('EXCELLENT', 'GOOD', 'FAIR')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SOLD', 'INACTIVE', 'DELETED')),
    view_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    -- Foreign Key constraints (without CASCADE)
    CONSTRAINT fk_products_seller 
        FOREIGN KEY (seller_id) REFERENCES users(id),
    
    -- Additional constraints
    CONSTRAINT chk_products_price_positive CHECK (price >= 0),
    CONSTRAINT chk_products_shipping_cost_positive CHECK (shipping_cost IS NULL OR shipping_cost >= 0),
    CONSTRAINT chk_products_view_count_positive CHECK (view_count >= 0),
    CONSTRAINT chk_products_title_not_empty CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_products_description_not_empty CHECK (LENGTH(TRIM(description)) > 0)
);

-- Indexes for performance
CREATE INDEX idx_products_seller_id ON products(seller_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
CREATE INDEX idx_products_price ON products(price);

-- Composite indexes for common queries
CREATE INDEX idx_products_status_category ON products(status, category);
CREATE INDEX idx_products_seller_status ON products(seller_id, status);