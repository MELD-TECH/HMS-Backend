-- ===========================================================
-- Additional HMS Security Roles
-- ===========================================================

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'SECURITY_ADMIN',
'Security Approval Administrator',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='SECURITY_ADMIN');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'RECEPTIONIST',
'Reception Desk',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='RECEPTIONIST');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'LAB_TECHNICIAN',
'Laboratory Technician',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='LAB_TECHNICIAN');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'RADIOLOGIST',
'Radiology Department',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='RADIOLOGIST');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'CASHIER',
'Cash Office',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='CASHIER');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'RECORDS_OFFICER',
'Medical Records',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='RECORDS_OFFICER');

INSERT INTO identity_schema.roles
(id,name,description,created_at,version,created_by)

SELECT gen_random_uuid(),'HMO_OFFICER',
'HMO Officer',
CURRENT_TIMESTAMP,0,'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1
    FROM identity_schema.roles
    WHERE name='HMO_OFFICER');