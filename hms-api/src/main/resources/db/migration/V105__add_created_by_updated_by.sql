------------------------------------------------------------
-- COMMON AUDIT COLUMNS
------------------------------------------------------------

ALTER TABLE identity_schema.users
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.users
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

UPDATE identity_schema.users
SET created_by='SYSTEM'
WHERE created_by IS NULL;

ALTER TABLE identity_schema.roles
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.roles
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.permissions
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.permissions
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);


ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE notification_schema.otp_codes
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE notification_schema.otp_codes
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.audit_logs
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.audit_logs
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.refresh_tokens
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.refresh_tokens
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.pending_authentications
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.pending_authentications
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.password_history
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.password_history
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

ALTER TABLE identity_schema.password_reset_tokens
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);

ALTER TABLE identity_schema.password_reset_tokens
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

UPDATE identity_schema.permissions
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.roles
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE patient_schema.patients
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE notification_schema.otp_codes
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.audit_logs
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.refresh_tokens
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.pending_authentications
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.password_history
SET created_by='SYSTEM'
WHERE created_by IS NULL;

UPDATE identity_schema.password_reset_tokens
SET created_by='SYSTEM'
WHERE created_by IS NULL;