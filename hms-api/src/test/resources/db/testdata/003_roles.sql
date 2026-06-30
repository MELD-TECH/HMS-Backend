DELETE FROM identity_schema.user_roles
WHERE user_id =
(
    SELECT id
    FROM identity_schema.users
    WHERE username='admin'
);

INSERT INTO identity_schema.user_roles
(
    user_id,
    role_id
)

SELECT
u.id,
r.id

FROM identity_schema.users u,
     identity_schema.roles r

WHERE

u.username='admin'

AND

r.name='SUPER_ADMIN';