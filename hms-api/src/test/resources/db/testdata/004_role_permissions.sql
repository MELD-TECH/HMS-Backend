INSERT INTO identity_schema.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM identity_schema.roles r
CROSS JOIN identity_schema.permissions p
WHERE r.name = 'SUPER_ADMIN'
AND NOT EXISTS (
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
);
