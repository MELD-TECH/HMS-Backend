INSERT INTO identity_schema.permissions
(
    id,
    code,
    description,
    created_at,
    version
)
VALUES
(
    gen_random_uuid(),
    'AUDIT_VIEW',
    'View Audit Logs',
    now(),
    0
),
(
    gen_random_uuid(),
    'AUDIT_EXPORT',
    'Export Audit Logs',
    now(),
    0
);