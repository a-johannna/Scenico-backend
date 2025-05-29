# Imagen base con Java 17
FROM eclipse-temurin:17-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el .jar generado
COPY target/demo1-0.0.1-SNAPSHOT.jar app.jar

# Expón el puerto 8080 que usa Spring Boot
EXPOSE 8080

# Ejecuta el jar
ENTRYPOINT ["java", "-jar", "app.jar"]
