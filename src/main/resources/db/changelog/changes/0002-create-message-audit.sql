--liquibase formatted sql
--changeset jarvis:0002-create-message-audit
CREATE TABLE message_audit (
    id uuid NOT NULL,
    chat_id bigint NOT NULL,
    message_id bigint,
    direction varchar(20) NOT NULL,
    source varchar(30) NOT NULL,
    status varchar(20) NOT NULL,
    request_text text,
    response_text text,
    error_type varchar(100),
    error_message text,
    correlation_id uuid,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE INDEX idx_message_audit_chat_id ON message_audit(chat_id);
CREATE INDEX idx_message_audit_created_at ON message_audit(created_at);
CREATE INDEX idx_message_audit_direction ON message_audit(direction);

