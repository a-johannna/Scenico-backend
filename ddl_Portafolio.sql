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

select * from user.portafolios;


select * from portafolios;

select * FROM portafolios p
                  JOIN user u ON p.id_user = u.id_user
WHERE p.id_portafolio = 25;

INSERT INTO portafolios (
    id_portafolio,
    titulo,
    descripcion,
    tipo_archivo,
    url_archivo,
    url_imagen,
    nombre_imagen,
    descripcion_imagen,
    etiquetas,
    fecha_creacion,
    id_user
) VALUES (
             -- Solo valores
    25,
             'Mi primer portafolio',
             'Exploración visual del teatro contemporáneo.',
             'IMAGE',
             'https://escenico.com/archivos/video-presentacion.mp4',
             'https://escenico.com/imagenes/portada.jpg',
             'portada.jpg',
             'Presentación principal del portafolio artístico.',
             'teatro,danza,visual',
             NOW(),
          25

         );

ALTER TABLE portafolio_etiquetas
    DROP FOREIGN KEY FK9hgawtpi7iv5u9qcne7y9n1x9;


ALTER TABLE portafolios
    MODIFY COLUMN id_portafolio BIGINT NOT NULL AUTO_INCREMENT;


ALTER TABLE portafolio_etiquetas
    ADD CONSTRAINT FK9hgawtpi7iv5u9qcne7y9n1x9
        FOREIGN KEY (id_portafolio) REFERENCES portafolios(id_portafolio);
SHOW COLUMNS FROM portafolios;
SHOW CREATE TABLE portafolio_etiquetas;
ALTER TABLE portafolio_etiquetas
    DROP INDEX FK9hgawtpi7iv5u9qcne7y9n1x9;

ALTER TABLE portafolio_etiquetas
    ADD CONSTRAINT fk_portafolio_etiqueta
        FOREIGN KEY (portafolio_id_portafolio)
            REFERENCES portafolios(id_portafolio)
            ON DELETE CASCADE;

