-- Add birth_date and audit columns to users table
ALTER TABLE users ADD COLUMN birth_date DATE;
ALTER TABLE users ADD COLUMN created_by_user_id UUID;
ALTER TABLE users ADD COLUMN updated_by_user_id UUID;
ALTER TABLE users ADD COLUMN created_ip VARCHAR(45);
ALTER TABLE users ADD COLUMN updated_ip VARCHAR(45);

-- Create babies table
CREATE TABLE babies (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    birth_date DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_user_id UUID,
    updated_by_user_id UUID,
    created_ip VARCHAR(45),
    updated_ip VARCHAR(45)
);

-- Create baby_interests table
CREATE TABLE baby_interests (
    baby_id UUID NOT NULL REFERENCES babies(id) ON DELETE CASCADE,
    interest_category VARCHAR(50) NOT NULL CHECK (
        interest_category IN (
            'BABY_CLOTHING',
            'GIRLS_CLOTHING', 
            'BOYS_CLOTHING',
            'BABY_PRODUCTS',
            'TOYS_EDUCATIONAL',
            'FEEDING_WEANING',
            'MOTHER_PRODUCTS'
        )
    ),
    PRIMARY KEY (baby_id, interest_category)
);

-- Create user_regions table
CREATE TABLE user_regions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    region_code VARCHAR(10) NOT NULL,
    sido VARCHAR(50) NOT NULL,
    sigungu VARCHAR(50) NOT NULL,
    dong VARCHAR(50),
    priority INTEGER NOT NULL CHECK (priority BETWEEN 1 AND 3),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_user_id UUID,
    updated_by_user_id UUID,
    created_ip VARCHAR(45),
    updated_ip VARCHAR(45),
    UNIQUE(user_id, region_code),
    UNIQUE(user_id, priority)
);

-- Add indexes for better performance
CREATE INDEX idx_babies_user_id ON babies(user_id);
CREATE INDEX idx_babies_birth_date ON babies(birth_date);
CREATE INDEX idx_baby_interests_baby_id ON baby_interests(baby_id);
CREATE INDEX idx_user_regions_user_id ON user_regions(user_id);
CREATE INDEX idx_user_regions_priority ON user_regions(user_id, priority);
CREATE INDEX idx_user_regions_location ON user_regions(sido, sigungu);