# 🎭 Scénico - Plataforma para Artistas Emergentes

**Scénico** es una plataforma web desarrollada como Trabajo de Fin de Grado (TFG) para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM).
Su objetivo principal es ofrecer un entorno profesional, seguro y especializado para que artistas emergentes puedan visibilizar su trabajo y conectar con empresas, agencias, promotores y agentes del sector cultural.

## 🧩 Características principales

- Creación y gestión de portafolios digitales para artistas
- Publicación de oportunidades por parte de empresas y organizaciones culturales
- Postulación directa de artistas a ofertas
- Verificación manual de perfiles para garantizar autenticidad
- Herramientas de interacción: seguimiento, mensajes privados, comentarios
- Sistema de autenticación segura con JWT y control de acceso por roles
- Vista pública de oportunidades para usuarios no registrados

---

## 🛠️ Tecnologías utilizadas

### Backend
- Java 17
- Spring Boot
- Spring Security + JWT
- JPA/Hibernate
- MySQL
- Maven

### Frontend
- Angular 17
- TypeScript
- Bootstrap / Angular Material
- RxJS

### Otros recursos
- IntelliJ IDEA Ultimate (desarrollo y conexión a BBDD)
- Postman (pruebas de API)
- DevTools (depuración)
- Javadoc (documentación)
- Railway (hosting)
- Figma, draw.io y Canva (diseño visual y diagramas)


---

## 🚀 Ejecución del proyecto

### Backend

```bash
# Clona el repositorio
git clone https://github.com/a-johannna/Scenico-backend.git
cd scenico-backend

# Configura application.properties con tu base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/scenico_db
spring.datasource.username=usuario
spring.datasource.password=contraseña

# Ejecuta el backend
./mvnw spring-boot:run
