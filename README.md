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
