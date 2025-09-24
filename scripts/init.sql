-- Database initialization script for bebe-place-api
-- This script sets up the initial database configuration

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Set timezone
SET timezone = 'Asia/Seoul';

-- Create database if it doesn't exist (this won't work in PostgreSQL directly)
-- The database is already created by the Docker compose environment variables