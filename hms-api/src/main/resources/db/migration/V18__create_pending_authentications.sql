CREATE TABLE identity_schema.pending_authentications
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    username VARCHAR(100) NOT NULL,

    mfa_type VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    challenge_token VARCHAR(200) NOT NULL UNIQUE,

    expires_at TIMESTAMP NOT NULL,

    completed_at TIMESTAMP,

    ip_address VARCHAR(100),

    user_agent TEXT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    version BIGINT DEFAULT 0
);

CREATE INDEX idx_pending_auth_user
ON identity_schema.pending_authentications(user_id);

CREATE INDEX idx_pending_auth_token
ON identity_schema.pending_authentications(challenge_token);

CREATE INDEX idx_pending_auth_status
ON identity_schema.pending_authentications(status);

CREATE INDEX idx_pending_auth_expiry
ON identity_schema.pending_authentications(expires_at);