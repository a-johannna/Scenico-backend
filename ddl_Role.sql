CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(20)           NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

INSERT INTO roles (name) VALUES ('USER');
