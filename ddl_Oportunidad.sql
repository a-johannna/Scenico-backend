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

INSERT INTO oportunidades (
    titulo,
    descripcion,
    categoria,
    requisitos,
    ubicacion,
    fecha,
    fecha_cierre,
    estado_oportunidad,
    id_empresa
) VALUES (
             'Audición para Obra Teatral',
             'Buscamos actores para una obra contemporánea. Se requiere experiencia previa en teatro.',
             'Teatro',
             'Experiencia mínima de 2 años en actuación, disponibilidad para ensayos por las tardes.',
             'Barcelona',
             NOW(),
             DATE_ADD(NOW(), INTERVAL 30 DAY),
             'ACTIVA',  -- Asegúrate de que este valor coincida con los definidos en tu Enum EstadoOportunidad
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
