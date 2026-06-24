INSERT INTO identity_schema.permissions
(code, description, id, created_at, version)
VALUES
('ROLE_UPDATE', 'Update Role', gen_random_uuid(), now(), 0),
('PERMISSION_CREATE', 'Create Permission', gen_random_uuid(), now(), 0),
('PERMISSION_VIEW', 'View Permission', gen_random_uuid(), now(), 0)
;