ALTER TABLE identity_schema.audit_logs

ADD COLUMN module VARCHAR(100),

ADD COLUMN before_json TEXT,

ADD COLUMN after_json TEXT,

ADD COLUMN user_agent VARCHAR(500);