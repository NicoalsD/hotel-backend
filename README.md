# Hotel Reservas - Backend

API REST del **Sistema de Reservas de Hotel**, construida con Spring Boot y Java 17. El estado se maneja en memoria con colecciones de Java.

## Ejecución local

```bash
./mvnw spring-boot:run
```

El servidor queda disponible en `http://localhost:8080`.

## Descripción

Backend del sistema de reservas de un hotel. Permite consultar disponibilidad y tarifas por temporada, crear reservas, agregar servicios adicionales (spa, desayuno, traslado), realizar el check-in con llave digital, y el check-out con generación de factura con IVA. Toda la información vive en memoria; no hay base de datos.

## Tecnologías

- **Java 17**
- **Spring Boot 4.0.8** (starter web MVC, validation y actuator)
- **Maven** con el wrapper `./mvnw`
- **springdoc / Swagger UI**: documentación interactiva de la API en `/swagger-ui.html`
- **JUnit 5 y AssertJ** para las pruebas unitarias
