CREATE TABLE portafolios
(
    id_portafolio      BIGINT       NOT NULL,
    titulo             VARCHAR(255) NULL,
    descripcion        TEXT         NULL,
    tipo_archivo       VARCHAR(255) NULL,
    url_archivo        VARCHAR(255) NULL,
    url_imagen         VARCHAR(255) NULL,
    nombre_imagen      VARCHAR(255) NULL,
    descripcion_imagen VARCHAR(255) NULL,
    etiquetas          VARCHAR(255) NULL,
    fecha_creacion     datetime     NULL,
    id_user            BIGINT       NOT NULL,
    CONSTRAINT pk_portafolios PRIMARY KEY (id_portafolio)
);

ALTER TABLE portafolios
    ADD CONSTRAINT FK_PORTAFOLIOS_ON_IDUSER FOREIGN KEY (id_user) REFERENCES user (id_user);