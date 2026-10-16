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
- **springdoc / Swagger UI** (planificado): documentación interactiva de la API en `/swagger-ui.html`. Aún no está incluido en el `pom.xml` de esta rama.
- **JUnit 5 y AssertJ** para las pruebas unitarias

## Arquitectura por capas

```
Controlador REST (HotelController)         -> solo conoce la fachada
        |
Fachada (HotelFacade)                      -> punto de entrada único a los subsistemas
        |
Servicios (RoomService, RateService, AdditionalServiceService,
           BillingService, AccessService)  -> lógica de negocio
        |
Repositorios en memoria + modelo de dominio
```

- **Controlador**: recibe y responde HTTP, valida DTOs de entrada.
- **Fachada**: orquesta los subsistemas para cada caso de uso (crear reserva, check-in, check-out, etc.).
- **Servicios**: cada uno encapsula un subsistema.
- **Modelo y repositorios**: entidades (`Room`, `Reservation`, `Invoice`, ...) y almacenamiento en colecciones concurrentes.

Detalle en [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md).
