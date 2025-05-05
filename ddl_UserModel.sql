CREATE TABLE user_role
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (role_id, user_id)
);

CREATE TABLE IF NOT EXISTS user
(
    id_user       BIGINT       NOT NULL,
    uuid          BINARY(16)   NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    first_name    VARCHAR(50)  NULL,
    last_name     VARCHAR(50)  NULL,
    email         VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NULL,
    type_user     VARCHAR(255) NULL,
    verified      BIT(1)       NOT NULL,
    location      VARCHAR(255) NULL,
    photo_profile VARCHAR(255) NULL,
    created_at    datetime     NULL,
    update_at     datetime     NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id_user)
)
;

ALTER TABLE user
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE user
ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE user
    ADD CONSTRAINT uc_users_uuid UNIQUE (uuid);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_user_model FOREIGN KEY (user_id) REFERENCES user (id_user);

