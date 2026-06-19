CREATE TABLE identity_schema.roles
(
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,

    description TEXT,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE identity_schema.permissions
(
    id UUID PRIMARY KEY,

    code VARCHAR(150) NOT NULL UNIQUE,

    description TEXT,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE identity_schema.user_roles
(
    user_id UUID NOT NULL,

    role_id UUID NOT NULL,

    PRIMARY KEY(user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY(user_id)
        REFERENCES identity_schema.users(id),

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY(role_id)
        REFERENCES identity_schema.roles(id)
);

CREATE TABLE identity_schema.role_permissions
(
    role_id UUID NOT NULL,

    permission_id UUID NOT NULL,

    PRIMARY KEY(role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY(role_id)
        REFERENCES identity_schema.roles(id),

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY(permission_id)
        REFERENCES identity_schema.permissions(id)
);

INSERT INTO identity_schema.roles
(
    id,
    name,
    description,
    created_at,
    version
)
VALUES
(
    gen_random_uuid(),
    'SUPER_ADMIN',
    'System Administrator',
    now(),
    0
),
(
    gen_random_uuid(),
    'ADMIN',
    'Hospital Administrator',
    now(),
    0
),
(
    gen_random_uuid(),
    'DOCTOR',
    'Medical Doctor',
    now(),
    0
),
(
    gen_random_uuid(),
    'NURSE',
    'Nursing Staff',
    now(),
    0
),
(
    gen_random_uuid(),
    'PHARMACIST',
    'Pharmacy Staff',
    now(),
    0
);


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
    'USER_CREATE',
    'Create User',
    now(),
    0
),
(
    gen_random_uuid(),
    'USER_VIEW',
    'View User',
    now(),
    0
),
(
    gen_random_uuid(),
    'USER_UPDATE',
    'Update User',
    now(),
    0
),
(
    gen_random_uuid(),
    'USER_DISABLE',
    'Disable User',
    now(),
    0
),
(
    gen_random_uuid(),
    'ROLE_CREATE',
    'Create Role',
    now(),
    0
),
(
    gen_random_uuid(),
    'ROLE_VIEW',
    'View Role',
    now(),
    0
);