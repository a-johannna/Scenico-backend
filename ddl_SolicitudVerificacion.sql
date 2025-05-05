CREATE TABLE solicitudes_verificaciones
(
    id_solicitud        BIGINT       NOT NULL,
    user                BIGINT       NOT NULL,
    descripcion         TEXT         NOT NULL,
    archivo_demo_url    VARCHAR(255) NOT NULL,
    date_solicitud      datetime     NULL,
    fecha_resolucion    datetime     NULL,
    estado_solicitud    VARCHAR(255) NOT NULL,
    observaciones_admin VARCHAR(255) NULL,
    rol_solicitado      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_solicitudes_verificaciones PRIMARY KEY (id_solicitud)
);

ALTER TABLE solicitudes_verificaciones
    ADD CONSTRAINT FK_SOLICITUDES_VERIFICACIONES_ON_USER FOREIGN KEY (user) REFERENCES user (id_user);