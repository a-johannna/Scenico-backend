CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(20)           NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

INSERT INTO roles (name) VALUES ('USER');

INSERT INTO roles (name) VALUES ('ENTERPRISE');

select * from user;

UPDATE roles SET name = 'USER' WHERE name = 'ROLE_USER';
UPDATE roles SET name = 'ARTIST' WHERE name = 'ROLE_ARTIST';
UPDATE roles SET name = 'ADMIN' WHERE name = 'ROLE_ADMIN';

select * from user_role;
