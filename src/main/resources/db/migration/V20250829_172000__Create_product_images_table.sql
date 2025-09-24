-- Product Images table creation
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_order INTEGER NOT NULL,
    is_main BOOLEAN NOT NULL DEFAULT false,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key constraint without CASCADE
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products(id),
        
    -- Business constraints
    CONSTRAINT chk_product_images_order_positive CHECK (image_order > 0),
    CONSTRAINT chk_product_images_file_size_positive CHECK (file_size > 0),
    CONSTRAINT chk_product_images_url_not_empty CHECK (LENGTH(TRIM(image_url)) > 0),
    CONSTRAINT chk_product_images_filename_not_empty CHECK (LENGTH(TRIM(original_filename)) > 0),
    
    -- Unique constraints
    CONSTRAINT uk_product_images_product_order 
        UNIQUE(product_id, image_order)
);

-- Indexes for performance
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_is_main ON product_images(is_main) WHERE is_main = true;
CREATE INDEX idx_product_images_order ON product_images(product_id, image_order);
CREATE INDEX idx_product_images_created_at ON product_images(created_at DESC);

-- Ensure only one main image per product
CREATE UNIQUE INDEX uk_product_images_one_main_per_product 
ON product_images(product_id) 
WHERE is_main = true;