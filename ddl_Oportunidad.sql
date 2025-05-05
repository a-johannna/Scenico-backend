CREATE TABLE oportunidades
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    titulo             VARCHAR(100)          NULL,
    descripcion        TEXT                  NULL,
    categoria          VARCHAR(255)          NULL,
    requisitos         VARCHAR(500)          NULL,
    ubicacion          VARCHAR(255)          NULL,
    fecha              datetime              NULL,
    fecha_cierre       datetime              NULL,
    estado_oportunidad VARCHAR(255)          NULL,
    id_empresa         BIGINT                NOT NULL,
    CONSTRAINT pk_oportunidades PRIMARY KEY (id)
);

ALTER TABLE oportunidades
    ADD CONSTRAINT FK_OPORTUNIDADES_ON_IDEMPRESA FOREIGN KEY (id_empresa) REFERENCES user (id_user);