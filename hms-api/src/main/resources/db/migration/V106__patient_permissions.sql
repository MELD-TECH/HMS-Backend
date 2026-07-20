------------------------------------------------------------
-- PATIENT PERMISSIONS
------------------------------------------------------------

INSERT INTO identity_schema.permissions
(
    id,
    code,
    description,
    created_at,
    created_by,
    version
)
VALUES

(gen_random_uuid(),'PATIENT_CREATE','Register Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_VIEW','View Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_UPDATE','Update Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_SEARCH','Search Patients',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_DELETE','Delete Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_ARCHIVE','Archive Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_RESTORE','Restore Patient',CURRENT_TIMESTAMP,'SYSTEM',0),

(gen_random_uuid(),'PATIENT_EXPORT','Export Patient Records',CURRENT_TIMESTAMP,'SYSTEM',0);