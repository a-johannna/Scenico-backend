# Usa la imagen oficial de OpenJDK como base
FROM openjdk:17-jdk-slim

# Crea el directorio de la app en el contenedor
WORKDIR /app

# Copia el .jar desde el build local al contenedor
COPY target/demo1-0.0.1-SNAPSHOT.jar app.jar

# Expón el puerto que usa Spring Boot
EXPOSE 8080

# Ejecuta el .jar al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
