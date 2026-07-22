-- ===========================================================
-- User Role Mapping
-- ===========================================================

INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='SUPER_ADMIN'
WHERE u.username='super-admin'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='ADMIN'
WHERE u.username='admin'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='SECURITY_ADMIN'
WHERE u.username='security-admin'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='DOCTOR'
WHERE u.username='doctor'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='NURSE'
WHERE u.username='nurse'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='RECEPTIONIST'
WHERE u.username='receptionist'

ON CONFLICT DO NOTHING;



INSERT INTO identity_schema.user_roles(user_id,role_id)

SELECT
u.id,
r.id
FROM identity_schema.users u
JOIN identity_schema.roles r
ON r.name='PHARMACIST'
WHERE u.username='pharmacist'

ON CONFLICT DO NOTHING;