-- Drop the tables and components if they exist
DROP TABLE IF EXISTS REMINDERS;
DROP SEQUENCE IF EXISTS REMINDERS_SEQ;
DROP TABLE IF EXISTS TOKENS;
DROP SEQUENCE IF EXISTS TOKENS_SEQ;

-- Create the reminders table
CREATE SEQUENCE REMINDERS_SEQ start 1 increment 1;
CREATE TABLE REMINDERS (
    id int8 NOT NULL,
    created_at TIMESTAMP NOT NULL,
    task_id VARCHAR(120) NOT NULL,
    conversation_id VARCHAR(120),
    task VARCHAR(250),
    scheduled_at TIMESTAMP,
    scheduled_cron VARCHAR(120),
    is_eternal BOOL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_REMINDERS_CONVERSATION_ID ON REMINDERS (conversation_id);
CREATE INDEX IDX_REMINDERS_TASK_ID ON REMINDERS (task_id);

-- Create the tokens table
CREATE SEQUENCE TOKENS_SEQ start 1 increment 1;
CREATE TABLE TOKENS (
    id int8 NOT NULL,
    conversation_id VARCHAR(120) NOT NULL,
    token VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_TOKENS_CONVERSATION_ID ON TOKENS (conversation_id);

-- Dummy query alive check
SELECT 1;
