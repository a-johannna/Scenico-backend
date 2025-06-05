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

ALTER TABLE oportunidades CHANGE id_Empresa id_user BIGINT;

ALTER TABLE oportunidades
    ADD CONSTRAINT FK_OPORTUNIDADES_ON_IDEMPRESA FOREIGN KEY (id_user) REFERENCES user (id_user);

INSERT INTO oportunidades (
    titulo,
    descripcion,
    categoria,
    requisitos,
    ubicacion,
    fecha,
    fecha_cierre,
    estado_oportunidad,
    id_user
) VALUES (
             'Colaboración son SONY Productions',
             'Buscamos actores para una película. Se requiere experiencia previa. Perfiles entre 19 y 28 años',
             'Cine',
             'Experiencia mínima de 2 años en actuación, disponibilidad para ensayos por las tardes.',
             'Madrid',
             NOW(),
             DATE_ADD(NOW(), INTERVAL 30 DAY),
             'ACTIVA',
            101
         );

select * from oportunidades;

SELECT u.*
FROM user u
         JOIN oportunidades o ON u.id_user = o.id_empresa
WHERE o.titulo = 'Audición para Obra Teatral'
  AND o.descripcion LIKE '%Buscamos actores%'
  AND o.categoria = 'Teatro'
  AND o.ubicacion = 'Barcelona'
  AND o.estado_oportunidad = 'ACTIVA'
  AND o.id_empresa = 101;

SELECT u.*
FROM user u
         JOIN oportunidades o ON u.id_user = o.id_empresa
WHERE o.id_empresa = 101;


ALTER TABLE oportunidades
    CHANGE COLUMN id_empresa id_user BIGINT;

SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'oportunidades'
  AND COLUMN_NAME = 'id_user';

ALTER TABLE oportunidades
    DROP FOREIGN KEY FK_OPORTUNIDADES_ON_IDEMPRESA, -- cámbialo por el nombre real si es diferente
    ADD CONSTRAINT fk_oportunidad_usuario
        FOREIGN KEY (id_user) REFERENCES user(id_user);

SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'oportunidades'
  AND CONSTRAINT_SCHEMA = 'user' -- tu nombre de base de datos
  AND REFERENCED_TABLE_NAME = 'user';

SELECT * FROM oportunidades WHERE id_user NOT IN (SELECT id_user FROM user);



SHOW CREATE TABLE oportunidades;
ALTER TABLE oportunidades
    DROP COLUMN id_empresa;
