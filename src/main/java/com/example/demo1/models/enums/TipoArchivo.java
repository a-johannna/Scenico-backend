/**
 * TipoArchivo.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Enumeración que define los tipos de archivos multimedia que los artistas
 * pueden subir como parte de su portafolio. Esta clasificación ayuda a organizar,
 * filtrar y visualizar mejor los contenidos publicados.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.models.enums;

/**
 * Enum que representa los distintos tipos de archivo que puede tener un portafolio artístico.
 */
public enum TipoArchivo {
    /**
     * Archivo de tipo video (ej. .mp4, .mov).
     */
    VIDEO,

    /**
     * Archivo de tipo audio (ej. .mp3, .wav).
     */
    AUDIO,

    /**
     * Archivo de tipo imagen (ej. .jpg, .png, .gif).
     */
    IMAGE,

    /**
     * Archivo de tipo documento (ej. .pdf, .docx).
     */
    DOCUMENT
}
