DELETE FROM identity_schema.refresh_tokens;

DELETE FROM identity_schema.password_reset_tokens;

DELETE FROM identity_schema.password_history;

DELETE FROM identity_schema.user_roles;

DELETE FROM identity_schema.users
WHERE username='admin';