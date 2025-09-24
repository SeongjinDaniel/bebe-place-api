-- Drop existing tables to recreate with sequence IDs
DROP TABLE IF EXISTS baby_interests;
DROP TABLE IF EXISTS babies;
DROP TABLE IF EXISTS user_regions;

-- Create sequences
CREATE SEQUENCE baby_sequence START 1 INCREMENT 1;
CREATE SEQUENCE user_region_sequence START 1 INCREMENT 1;

-- Recreate babies table with BIGSERIAL ID
CREATE TABLE babies (
    id BIGSERIAL PRIMARY KEY,
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

-- Set sequence to be used by the id column
ALTER TABLE babies ALTER COLUMN id SET DEFAULT nextval('baby_sequence');
ALTER SEQUENCE baby_sequence OWNED BY babies.id;

-- Recreate baby_interests table
CREATE TABLE baby_interests (
    baby_id BIGINT NOT NULL REFERENCES babies(id) ON DELETE CASCADE,
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

-- Recreate user_regions table with BIGSERIAL ID
CREATE TABLE user_regions (
    id BIGSERIAL PRIMARY KEY,
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

-- Set sequence to be used by the id column
ALTER TABLE user_regions ALTER COLUMN id SET DEFAULT nextval('user_region_sequence');
ALTER SEQUENCE user_region_sequence OWNED BY user_regions.id;

-- Recreate indexes for better performance
CREATE INDEX idx_babies_user_id ON babies(user_id);
CREATE INDEX idx_babies_birth_date ON babies(birth_date);
CREATE INDEX idx_baby_interests_baby_id ON baby_interests(baby_id);
CREATE INDEX idx_user_regions_user_id ON user_regions(user_id);
CREATE INDEX idx_user_regions_priority ON user_regions(user_id, priority);
CREATE INDEX idx_user_regions_location ON user_regions(sido, sigungu);