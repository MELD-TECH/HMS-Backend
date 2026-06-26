CREATE TABLE identity_schema.audit_logs
(
    id UUID PRIMARY KEY,

    username VARCHAR(100),

    action VARCHAR(100) NOT NULL,

    entity_name VARCHAR(100),

    entity_id VARCHAR(100),

    details TEXT,

    ip_address VARCHAR(100),

    created_at TIMESTAMP NOT NULL,

    version BIGINT DEFAULT 0
);