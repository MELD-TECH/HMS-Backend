ALTER TABLE identity_schema.pending_authentications

ADD COLUMN last_otp_sent_at TIMESTAMP;

ALTER TABLE identity_schema.pending_authentications

ADD COLUMN resend_count INTEGER NOT NULL DEFAULT 0;