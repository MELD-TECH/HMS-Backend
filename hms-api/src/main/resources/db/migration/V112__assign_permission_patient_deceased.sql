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
'PATIENT_DECEASED'
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
'PATIENT_DECEASED'
);