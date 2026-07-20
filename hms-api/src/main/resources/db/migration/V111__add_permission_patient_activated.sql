INSERT INTO identity_schema.permissions
(
    id,
    code,
    description,
    created_at,
    created_by,
    version
)
VALUES
(gen_random_uuid(),'PATIENT_ACTIVATE','Activate Patient',CURRENT_TIMESTAMP,'SYSTEM',0);

------------------------------------------------------------
-- SUPER ADMIN
------------------------------------------------------------

INSERT INTO identity_schema.role_permissions
(
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON TRUE
WHERE r.name='SUPER_ADMIN'
AND p.code IN
(
'PATIENT_ACTIVATE'
);

------------------------------------------------------------
-- ADMIN
------------------------------------------------------------

INSERT INTO identity_schema.role_permissions
(
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM identity_schema.roles r
JOIN identity_schema.permissions p
ON TRUE
WHERE r.name='ADMIN'
AND p.code IN
(
'PATIENT_ACTIVATE'
);