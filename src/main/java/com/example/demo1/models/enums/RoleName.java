/**
 * RoleName.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Enumeración que define los distintos roles de usuario dentro del sistema.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */

package com.example.demo1.models.enums;

    /**
     * Enum que define los distintos tipos de roles disponibles en la plataforma.
     * Cada rol determina los permisos y accesos de los usuarios dentro del sistema.
     *
     */
    public enum RoleName
    {
        /**
         * Usuario general que tiene acceso a la creación de portafolios.
         */
        USER,

        /**
         * Artistas verificados que además de subir portafolios, puede registrarse en oportunidades (castings).
         */
        ARTIST,

        /**
         * Empresa o productor que puede crear oportunidades laborales o creativas.
         */
        ADMIN,

        /**
         * Administrador del sistema, encargado de la moderación y verificación de cuentas.
         */
        ENTERPRISE
    }