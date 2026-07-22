INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_CREATE',
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_UPDATE',
    'PATIENT_CONTACT_DELETE',
    'PATIENT_CONTACT_VERIFY',
    'PATIENT_CONTACT_PRIMARY'
)
WHERE r.name='SUPER_ADMIN'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_CREATE',
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_UPDATE',
    'PATIENT_CONTACT_DELETE',
    'PATIENT_CONTACT_VERIFY',
    'PATIENT_CONTACT_PRIMARY'
)
WHERE r.name='ADMIN'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_CREATE',
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_UPDATE',
    'PATIENT_CONTACT_VERIFY',
    'PATIENT_CONTACT_PRIMARY'
)
WHERE r.name='DOCTOR'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_CREATE',
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_UPDATE',
    'PATIENT_CONTACT_DELETE',
    'PATIENT_CONTACT_VERIFY',
    'PATIENT_CONTACT_PRIMARY'
)
WHERE r.name='RECORDS_OFFICER'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_CREATE',
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_UPDATE',
    'PATIENT_CONTACT_PRIMARY'
)
WHERE r.name='RECEPTIONIST'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code IN
(
    'PATIENT_CONTACT_VIEW',
    'PATIENT_CONTACT_VERIFY'
)
WHERE r.name='NURSE'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='PHARMACIST'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='PHARMACIST'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);


INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='LAB_TECHNICIAN'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='RADIOLOGIST'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='CASHIER'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);

INSERT INTO identity_schema.role_permissions(role_id, permission_id)

SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON p.code='PATIENT_CONTACT_VIEW'
WHERE r.name='HMO_OFFICER'
AND NOT EXISTS
(
    SELECT 1
    FROM identity_schema.role_permissions rp
    WHERE rp.role_id=r.id
    AND rp.permission_id=p.id
);



