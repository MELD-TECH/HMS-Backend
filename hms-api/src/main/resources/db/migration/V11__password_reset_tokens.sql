CREATE TABLE identity_schema.password_reset_tokens
(
    id UUID PRIMARY KEY,

    token VARCHAR(100) UNIQUE NOT NULL,

    user_id UUID NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    used BOOLEAN DEFAULT FALSE,

    used_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    version BIGINT DEFAULT 0,

    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id)
        REFERENCES identity_schema.users(id)
);