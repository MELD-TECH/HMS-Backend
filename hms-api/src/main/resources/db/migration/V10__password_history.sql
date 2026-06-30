CREATE TABLE identity_schema.password_history
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    password_hash VARCHAR(255) NOT NULL,

    changed_at TIMESTAMP NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    version BIGINT DEFAULT 0,

    CONSTRAINT fk_password_history_user
        FOREIGN KEY (user_id)
        REFERENCES identity_schema.users(id)
);