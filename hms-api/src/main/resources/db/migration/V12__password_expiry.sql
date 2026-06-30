ALTER TABLE identity_schema.users

ADD COLUMN password_changed_at TIMESTAMP,

ADD COLUMN password_expires_at TIMESTAMP;