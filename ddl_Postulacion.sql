CREATE TABLE postulacion
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    id_artista         BIGINT                NOT NULL,
    id_oportunidad     BIGINT                NOT NULL,
    mensaje            TEXT                  NULL,
    fecha              datetime              NULL,
    estado_postulacion VARCHAR(255)          NULL,
    CONSTRAINT pk_postulacion PRIMARY KEY (id)
);

ALTER TABLE postulacion
    ADD CONSTRAINT FK_POSTULACION_ON_IDARTISTA FOREIGN KEY (id_artista) REFERENCES user (id_user);

ALTER TABLE postulacion
    ADD CONSTRAINT FK_POSTULACION_ON_IDOPORTUNIDAD FOREIGN KEY (id_oportunidad) REFERENCES oportunidades (id);