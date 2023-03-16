-- Creation of message table
CREATE TABLE IF NOT EXISTS message (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id BIGINT NOT NULL,
    question varchar(2048) NOT NULL,
    answer varchar(255) NOT NULL
);