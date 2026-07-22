-- ===========================================================
-- Integration Test Users
-- Password for ALL users = password
-- BCrypt:
-- $2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S
-- ===========================================================

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'super-admin',
    'superadmin@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'Super',
    'Administrator',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'super-admin'
);

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'security-admin',
    'securityadmin@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'Security',
    'Administrator',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'security-admin'
);

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'doctor',
    'doctor@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'John',
    'Doctor',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'doctor'
);

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'nurse',
    'nurse@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'Jane',
    'Nurse',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'nurse'
);

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'receptionist',
    'reception@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'Mary',
    'Reception',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'receptionist'
);

INSERT INTO identity_schema.users
(
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    created_at,
    updated_at,
    version,
    mfa_enabled,
    mfa_type,
    created_by
)
SELECT
    gen_random_uuid(),
    'pharmacist',
    'pharmacy@hms.com',
    '$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',
    'Peter',
    'Pharmacist',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE,
    'NONE',
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.users
    WHERE username = 'pharmacist'
);