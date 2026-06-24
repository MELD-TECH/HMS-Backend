------------------------------------------------------------
-- TEST ADMIN USER
------------------------------------------------------------

-- Create admin user if not exists

INSERT INTO identity_schema.users (
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version
)
SELECT
    gen_random_uuid(),
    'admin',
    'admin@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'System',
    'Administrator',
    'ACTIVE',
    now(),
    now(),
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'admin'
);

------------------------------------------------------------
-- ASSIGN SUPER_ADMIN ROLE TO ADMIN USER
------------------------------------------------------------

INSERT INTO identity_schema.user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
    ON r.name = 'SUPER_ADMIN'
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM identity_schema.user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);

------------------------------------------------------------
-- ASSIGN ALL PERMISSIONS TO SUPER_ADMIN
------------------------------------------------------------

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

------------------------------------------------------------
-- VERIFICATION
------------------------------------------------------------
-- admin
-- password = password
-- role = SUPER_ADMIN
-- permissions = ALL
------------------------------------------------------------