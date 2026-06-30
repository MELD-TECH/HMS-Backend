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