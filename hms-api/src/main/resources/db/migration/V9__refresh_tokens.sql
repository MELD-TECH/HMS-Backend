CREATE TABLE identity_schema.refresh_tokens
(
    id UUID PRIMARY KEY,

    token VARCHAR(200) NOT NULL UNIQUE,

    user_id UUID NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    revoked BOOLEAN DEFAULT FALSE,

    revoked_at TIMESTAMP,

    last_used_at TIMESTAMP,

    device_id VARCHAR(100),

    device_name VARCHAR(100),

    ip_address VARCHAR(100),

    user_agent VARCHAR(500),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    version BIGINT DEFAULT 0,

    CONSTRAINT fk_refresh_user
        FOREIGN KEY(user_id)
        REFERENCES identity_schema.users(id)
);