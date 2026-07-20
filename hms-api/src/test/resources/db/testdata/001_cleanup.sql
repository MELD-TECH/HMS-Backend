DELETE FROM identity_schema.refresh_tokens;

DELETE FROM identity_schema.password_reset_tokens;

DELETE FROM identity_schema.password_history;

DELETE FROM identity_schema.user_roles;

DELETE FROM identity_schema.users
WHERE username='admin';

DELETE FROM patient_schema.patients;

UPDATE identity_schema.users

SET

failed_login_attempts = 0,

account_locked = false,

locked_at = NULL,

lock_expires_at = NULL,

last_login_at = NULL,

password_hash =
'$2a$10$kNNqD3Qjqh6SGGZMKJoUtuaU0O1jjO2WMRULrcRa5feFnp//WNN5S',

password_changed_at = now(),

password_expires_at = now() + interval '90 days',

updated_at = now()

WHERE username IN
(
    'security-admin'
);