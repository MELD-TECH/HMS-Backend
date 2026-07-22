INSERT INTO identity_schema.password_history
(
id,
user_id,
password_hash,
created_at,
created_by
)

SELECT
gen_random_uuid(),
id,
password_hash,
CURRENT_TIMESTAMP,
'SYSTEM'

FROM identity_schema.users

WHERE username IN
(
'admin',
'super-admin',
'security-admin',
'doctor',
'nurse',
'receptionist',
'pharmacist'
)

ON CONFLICT DO NOTHING;