INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_CREATE',
    'Create patient contact',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_CREATE'
);

INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_VIEW',
    'View patient contacts',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_VIEW'
);

INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_UPDATE',
    'Update patient contact',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_UPDATE'
);

INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_DELETE',
    'Delete patient contact',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_DELETE'
);

INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_VERIFY',
    'Verify patient contact',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_VERIFY'
);

INSERT INTO identity_schema.permissions(code, description, id, created_at, version)
SELECT
    'PATIENT_CONTACT_PRIMARY',
    'Change primary patient contact',
    gen_random_uuid(), 
    now(), 
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.permissions
    WHERE code='PATIENT_CONTACT_PRIMARY'
);