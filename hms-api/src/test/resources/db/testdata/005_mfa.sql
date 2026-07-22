UPDATE identity_schema.users
SET

mfa_enabled=false,
mfa_type='NONE',
mfa_secret=NULL

WHERE username IN
(
'admin',
'super-admin',
'security-admin',
'doctor',
'nurse',
'receptionist',
'pharmacist'
);