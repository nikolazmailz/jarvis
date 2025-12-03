--liquibase formatted sql
--changeset jarvis:0001-create-message-queue
CREATE TABLE message_queue (
    id uuid NOT NULL,
    chat_id bigint NOT NULL,
    message_text text NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'NEW',
    origin varchar(32) NOT NULL DEFAULT 'USER',
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
