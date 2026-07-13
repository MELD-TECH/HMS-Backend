UPDATE identity_schema.users
SET
    mfa_enabled = TRUE,
    mfa_type = 'EMAIL'
WHERE username='admin';