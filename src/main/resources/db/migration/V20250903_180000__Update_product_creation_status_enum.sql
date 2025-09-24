-- Update ProductCreationStatus enum and remove current_step column
-- Remove current_step column as it's now handled by the status enum
ALTER TABLE product_creation_logs DROP COLUMN IF EXISTS current_step;

-- Update status column to support new enum values
ALTER TABLE product_creation_logs 
DROP CONSTRAINT IF EXISTS product_creation_logs_status_check;

ALTER TABLE product_creation_logs 
ADD CONSTRAINT product_creation_logs_status_check 
CHECK (status IN (
    'PRODUCT_REGISTRATION_IN_PROGRESS',
    'IMAGE_UPLOAD_PENDING', 
    'COMPLETED',
    'FAILED'
));

-- Update existing data to map old status values to new ones
-- PROCESSING -> PRODUCT_REGISTRATION_IN_PROGRESS (default processing state)
UPDATE product_creation_logs 
SET status = 'PRODUCT_REGISTRATION_IN_PROGRESS' 
WHERE status = 'PROCESSING';

-- Add comment for clarity
COMMENT ON COLUMN product_creation_logs.status IS 'Product creation status with detailed steps: PRODUCT_REGISTRATION_IN_PROGRESS, IMAGE_UPLOAD_PENDING, COMPLETED, FAILED';