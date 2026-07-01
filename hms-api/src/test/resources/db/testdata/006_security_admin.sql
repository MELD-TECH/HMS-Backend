INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,

    failed_login_attempts,
    account_locked,

    password_changed_at,
    password_expires_at,

    created_at,
    updated_at,
    version
)

VALUES
(
    gen_random_uuid(),

    'security-admin',

    'security-admin@hms.com',

    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',

    'Security',

    'Administrator',

    'ACTIVE',

    0,

    false,

    now(),

    now() + interval '90 days',

    now(),

    now(),

    0
)
ON CONFLICT (username)
DO NOTHING;
