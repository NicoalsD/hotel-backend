# Hotel Reservas - Backend

API REST del **Sistema de Reservas de Hotel**, construida con Spring Boot y Java 17. El estado se maneja en memoria con colecciones de Java.

## Ejecución local

```bash
./mvnw spring-boot:run
```

El servidor queda disponible en `http://localhost:8080`.

## Descripción

Backend del sistema de reservas de un hotel. Permite consultar disponibilidad y tarifas por temporada, crear reservas, agregar servicios adicionales (spa, desayuno, traslado), realizar el check-in con llave digital, y el check-out con generación de factura con IVA. La información de trabajo vive en colecciones de Java; en producción se persiste en PostgreSQL (Neon), ver la sección [Persistencia en PostgreSQL (Neon)](#persistencia-en-postgresql-neon).

## Tecnologías

- **Java 17**
- **Spring Boot 4.0.8** (starter web MVC, validation y actuator)
- **Maven** con el wrapper `./mvnw`
- **springdoc / Swagger UI**: documentación interactiva de la API disponible en `/swagger-ui.html`.
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

## Patrones de diseño

| Patrón | Dónde | Propósito |
|---|---|---|
| **Facade** (obligatorio) | `HotelFacade` | Única entrada para el controlador; oculta los cinco subsistemas. |
| **Strategy** | `PricingStrategy`, `HighSeasonPricingStrategy`, `LowSeasonPricingStrategy` | Calcular el precio de la noche según la temporada. |
| **Factory** | `AdditionalServiceFactory` | Crear el servicio adicional correcto (`SpaService`, `BreakfastService`, `TransferService`). |
| **Repository** | `RoomRepository`, `ReservationRepository`, `InvoiceRepository` y sus implementaciones en memoria | Aislar el almacenamiento en colecciones de Java. |
| **DTO / Mapper** | paquetes `dto` y `mapper` | Separar el contrato JSON del modelo de dominio. |

Detalle en [docs/PATRONES.md](docs/PATRONES.md).

## Equivalencias requisito -> clase

Los nombres de los servicios y de los casos de uso se mantienen en español en los requisitos, y en inglés en el código.

| Requisito (español) | Clase / método (código) |
|---|---|
| HabitacionService | `RoomService` |
| TarifaService | `RateService` |
| ServicioAdicionalService | `AdditionalServiceService` |
| FacturacionService | `BillingService` |
| AccesoService | `AccessService` |
| crearReserva | `HotelFacade.createReservation` |
| agregarServicio | `HotelFacade.addService` |
| realizarCheckIn | `HotelFacade.performCheckIn` |
| realizarCheckOut | `HotelFacade.performCheckOut` |

## Endpoints

Base: `/api/hotel`. Detalle completo con ejemplos en [docs/API.md](docs/API.md).

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/disponibilidad?checkIn=&checkOut=&roomType=` | Habitaciones con bandera `available` y total estimado |
| GET | `/tarifa?roomType=&checkIn=&checkOut=` | Vista previa de precio noche a noche |
| GET | `/habitaciones` | Listado de las 16 habitaciones |
| POST | `/reservar` | Crea una reserva (201) |
| GET | `/reserva/{reservaId}` | Consulta una reserva |
| GET | `/reservas` | Lista reservas, la más reciente primero |
| POST | `/servicios/{reservaId}` | Agrega un servicio adicional |
| GET | `/servicios/catalogo` | Catálogo de servicios adicionales |
| PUT | `/checkin/{reservaId}` | Check-in y emisión de llave digital |
| PUT | `/checkout/{reservaId}` | Check-out y generación de factura |
| GET | `/factura/{reservaId}` | Factura de una reserva finalizada |

## Reglas de negocio

- **Temporada alta**: noches de junio, julio, diciembre y enero. Multiplicador 1.5 sobre el precio base.
- **Temporada baja**: resto del año. Multiplicador 1.0.
- **Precio por noche**: se calcula cada noche desde el check-in (incluido) hasta el check-out (excluido).
- **Tipos de habitación**: Sencilla (80.00), Doble (120.00) y Suite (250.00).
- **Servicios adicionales**: Spa (50.00, cobro fijo), Desayuno (15.00 por noche), Traslado (30.00, cobro fijo).
- **IVA**: 19% sobre el subtotal de habitaciones y servicios.
- **Disponibilidad**: una habitación está ocupada si tiene una reserva CONFIRMADA o con check-in que se traslape con el rango.

Detalle en [docs/REGLAS_NEGOCIO.md](docs/REGLAS_NEGOCIO.md).

## Cómo ejecutar y probar

Requisitos: JDK 17. El proyecto incluye el wrapper de Maven, no hace falta instalarlo.

```bash
# Ejecutar en local (http://localhost:8080)
./mvnw spring-boot:run

# Compilar y ejecutar todas las pruebas
./mvnw -B verify

# Ejemplo: consultar el catálogo de servicios
curl http://localhost:8080/api/hotel/servicios/catalogo
```

El puerto se configura con la variable `PORT` y los orígenes CORS permitidos con `CORS_ALLOWED_ORIGINS`.

## Persistencia en PostgreSQL (Neon)

La aplicación tiene dos modos de almacenamiento, elegidos por el perfil de Spring:

| Perfil | Activación | Almacenamiento |
|---|---|---|
| Por defecto | Sin configuración adicional | Solo en memoria (repositorios `InMemory*`). No necesita base de datos. |
| `postgres` | `SPRING_PROFILES_ACTIVE=postgres` | PostgreSQL (Neon), con los repositorios `Postgres*` del paquete `repository.postgres`. |

Variables de entorno que espera el perfil `postgres`:

| Variable | Descripción |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Debe valer `postgres` para activar la persistencia. |
| `SPRING_DATASOURCE_URL` | URL JDBC de la base de datos, por ejemplo `jdbc:postgresql://<host>/<base>?sslmode=require`. |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos. |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña del usuario. Nunca se escribe en el repositorio. |

Ejemplo de ejecución local contra una base de datos propia (los valores son marcadores, no credenciales reales):

```bash
export SPRING_PROFILES_ACTIVE=postgres
export SPRING_DATASOURCE_URL='jdbc:postgresql://<host>/<base>?sslmode=require'
export SPRING_DATASOURCE_USERNAME='<usuario>'
export SPRING_DATASOURCE_PASSWORD='<contraseña>'
./mvnw spring-boot:run
```

Comportamiento del perfil `postgres`:

- **Esquema**: `schema-postgres.sql` se ejecuta al arrancar con `CREATE TABLE IF NOT EXISTS`, así que es idempotente.
- **Carga al arrancar**: `DatabaseStateLoader` lee habitaciones, reservas (con servicios y llaves) y facturas, y reconstruye las reservas activas en cada habitación. Después avanza los generadores de identificadores para no repetir números. `RoomSeeder` solo siembra las 16 habitaciones si la tabla `rooms` está vacía.
- **Las colecciones en memoria siguen siendo el estado de trabajo**: las consultas se atienden desde las colecciones concurrentes (caché de escritura directa, *write-through*). Cada `save(...)` escribe también en PostgreSQL mediante un UPSERT con `JdbcTemplate`, de modo que los datos sobreviven a reinicios.

Sin las variables de entorno, el perfil `postgres` no arranca; el perfil por defecto no requiere ninguna de ellas.

## Despliegue en AWS Elastic Beanstalk

- URL de producción: http://hotel-backend-grupo.us-east-1.elasticbeanstalk.com
- Documentación Swagger UI: http://hotel-backend-grupo.us-east-1.elasticbeanstalk.com/swagger-ui.html
- Plantilla de infraestructura: [infra/cloudformation.json](infra/cloudformation.json). Pasos en [docs/DESPLIEGUE.md](docs/DESPLIEGUE.md).

## Integrantes

| Integrante | Responsabilidades |
|---|---|
| **NicoalsD** | Modelo de dominio, repositorios en memoria, servicio de habitaciones (`RoomService`), servicio de facturación (`BillingService`). |
| **santhiagoxinge** | Tarifas con Strategy (`RateService`, `PricingStrategy`), servicios adicionales con Factory (`AdditionalServiceService`), llaves digitales (`AccessService`), documentación. |
| **VictorAguilar202** | DTOs, mappers, manejo de errores, configuración de CORS, `HotelFacade`, controlador REST y Swagger. |
