CREATE SCHEMA IF NOT EXISTS notification_schema;

CREATE TABLE notification_schema.otp_codes
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    recipient VARCHAR(255) NOT NULL,

    code VARCHAR(10) NOT NULL,

    type VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    attempts INTEGER DEFAULT 0,

    resend_count INTEGER DEFAULT 0,

    expires_at TIMESTAMP NOT NULL,

    verified_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    version BIGINT DEFAULT 0
);