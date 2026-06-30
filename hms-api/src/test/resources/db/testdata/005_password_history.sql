INSERT INTO identity_schema.password_history
(
    id,
    user_id,
    password_hash,
    changed_at,
    created_at,
    version
)

SELECT

gen_random_uuid(),

id,

password_hash,

now(),

now(),

0

FROM identity_schema.users

WHERE username='admin';