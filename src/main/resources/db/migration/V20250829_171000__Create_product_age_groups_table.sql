-- Product Age Groups table creation (Many-to-Many without CASCADE)
CREATE TABLE product_age_groups (
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    age_group VARCHAR(20) NOT NULL CHECK (age_group IN (
        'NEWBORN_0_3', 'INFANT_4_7', 'INFANT_8_12', 'TODDLER_13_18', 
        'TODDLER_19_24', 'PRESCHOOL_3_4', 'PRESCHOOL_5_6', 'SCHOOL_7_PLUS'
    )),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key constraint without CASCADE
    CONSTRAINT fk_product_age_groups_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    
    -- Unique constraint to prevent duplicate age groups per product
    CONSTRAINT uk_product_age_groups_product_age 
        UNIQUE(product_id, age_group)
);

-- Indexes for performance
CREATE INDEX idx_product_age_groups_product_id ON product_age_groups(product_id);
CREATE INDEX idx_product_age_groups_age_group ON product_age_groups(age_group);
CREATE INDEX idx_product_age_groups_created_at ON product_age_groups(created_at DESC);