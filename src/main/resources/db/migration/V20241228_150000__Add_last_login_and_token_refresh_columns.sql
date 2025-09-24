-- Add last login and token refresh timestamp columns to users table
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE users ADD COLUMN last_token_refresh_at TIMESTAMP WITHOUT TIME ZONE;

-- Add index for last_login_at for performance queries
CREATE INDEX idx_users_last_login_at ON users(last_login_at);

-- Add comment for documentation
COMMENT ON COLUMN users.last_login_at IS 'Timestamp of the user''s last successful login';
COMMENT ON COLUMN users.last_token_refresh_at IS 'Timestamp of the user''s last access token refresh';