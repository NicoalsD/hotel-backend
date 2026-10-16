# Patrones de diseño

## Facade (obligatorio): `HotelFacade`

`HotelFacade` es la única clase que el controlador puede usar. Expone un método por caso de uso y oculta la coordinación entre `RoomService`, `RateService`, `AdditionalServiceService`, `BillingService`, `AccessService` y los repositorios.

Beneficios: el controlador no conoce los subsistemas, y un cambio interno (por ejemplo, otra regla de facturación) no obliga a tocar la capa HTTP.

Casos de uso expuestos:

| Método | Caso de uso |
|---|---|
| `createReservation` | crearReserva |
| `addService` | agregarServicio |
| `performCheckIn` | realizarCheckIn |
| `performCheckOut` | realizarCheckOut |
| `getReservation`, `listReservations` | consultas de reservas |
| `checkAvailability`, `quotePrice`, `listRooms` | consultas de disponibilidad, tarifas y habitaciones |
| `getServiceCatalog`, `getInvoice` | catálogo y factura |

## Strategy: tarifas por temporada

Interfaz `PricingStrategy` con dos métodos: `getSeason()` y `calculateNightlyPrice(basePrice)`.

- `HighSeasonPricingStrategy`: precio base multiplicado por 1.5.
- `LowSeasonPricingStrategy`: precio base sin cambios.

`SeasonCalendar` decide la temporada de cada fecha (enero, junio, julio y diciembre son temporada alta). `PricingStrategyResolver` recibe la lista de estrategias por inyección y devuelve la correcta para cada temporada. Así, agregar una nueva temporada implica agregar una clase, sin modificar `RateService`.

## Factory: servicios adicionales

`AdditionalServiceFactory.create(type, nights)` usa un `switch` sobre `AdditionalServiceType` y devuelve:

- `SpaService` (cantidad 1)
- `BreakfastService` (cantidad igual a las noches)
- `TransferService` (cantidad 1)

Todas son subclases de la clase abstracta `AdditionalService`, que delega en `calculateQuantity` la cantidad. El identificador (`SRV-n`) lo asigna `SequenceGenerator`.

## Repository: almacenamiento en memoria

Cada entidad principal tiene una interfaz y una implementación en memoria:

| Interfaz | Implementación | Estructura |
|---|---|---|
| `RoomRepository` | `InMemoryRoomRepository` | `ConcurrentHashMap` por id |
| `ReservationRepository` | `InMemoryReservationRepository` | `ConcurrentHashMap` por id de reserva |
| `InvoiceRepository` | `InMemoryInvoiceRepository` | `ConcurrentHashMap` por número de factura |

Los servicios dependen de la interfaz, por lo que una base de datos real podría reemplazar estas clases sin cambiar la lógica.

## DTO y Mapper

- Los DTO son `record` de Java y su nombre de campos coincide con el contrato de `API-CONTRACT.md`.
- Los mappers (`RoomMapper`, `PriceQuoteMapper`, `ReservationMapper`, `InvoiceMapper`, `GuestMapper`, `ServiceCatalogMapper`) solo convierten datos; no contienen reglas de negocio.
- El modelo de dominio nunca se expone directamente en la API.
