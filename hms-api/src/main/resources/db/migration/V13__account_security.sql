ALTER TABLE identity_schema.users

ADD COLUMN failed_login_attempts INTEGER NOT NULL DEFAULT 0,

ADD COLUMN account_locked BOOLEAN NOT NULL DEFAULT FALSE,

ADD COLUMN locked_at TIMESTAMP,

ADD COLUMN lock_expires_at TIMESTAMP;