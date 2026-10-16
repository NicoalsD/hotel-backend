# API REST

Base: `/api/hotel`. Fechas en formato `YYYY-MM-DD`. Fechas y horas en `YYYY-MM-DDTHH:mm:ss`. Montos con dos decimales.

Errores (formato común):

```json
{ "timestamp": "2026-10-16T15:04:05", "status": 409, "error": "Conflict", "message": "La habitación 201 no está disponible en las fechas seleccionadas", "path": "/api/hotel/reservar" }
```

| Código | Significado |
|---|---|
| 400 | Validación fallida o rango de fechas inválido |
| 404 | Reserva, habitación o factura no encontrada |
| 409 | Habitación no disponible o transición de estado inválida |
| 500 | Error interno del servidor |

## Disponibilidad y tarifas

### GET /api/hotel/disponibilidad

Parámetros: `checkIn`, `checkOut` (obligatorios) y `roomType` (opcional). Devuelve todas las habitaciones del tipo indicado con la bandera `available`.

Ejemplo: `GET /api/hotel/disponibilidad?checkIn=2026-11-01&checkOut=2026-11-04&roomType=DOUBLE`

Respuesta `200`:

```json
[{
  "roomId": 7, "number": "201", "type": "DOUBLE", "typeLabel": "Doble", "floor": 2, "capacity": 2,
  "basePrice": 120.00, "description": "Habitación doble con vista a la ciudad",
  "available": true, "nights": 3, "estimatedTotal": 360.00
}]
```

### GET /api/hotel/tarifa

Vista previa del precio. Parámetros: `roomType`, `checkIn`, `checkOut`.

Ejemplo: `GET /api/hotel/tarifa?roomType=DOUBLE&checkIn=2026-12-30&checkOut=2027-01-02`

Respuesta `200`:

```json
{
  "roomType": "DOUBLE", "typeLabel": "Doble", "checkIn": "2026-12-30", "checkOut": "2027-01-02",
  "nights": 3, "basePrice": 120.00, "highSeasonNights": 3, "lowSeasonNights": 0, "total": 540.00,
  "nightlyRates": [{"date": "2026-12-30", "season": "HIGH", "seasonLabel": "Temporada alta", "price": 180.00}]
}
```

## Habitaciones y reservas

### GET /api/hotel/habitaciones

Respuesta `200`: arreglo de habitaciones.

```json
[{ "id": 1, "number": "101", "type": "SINGLE", "typeLabel": "Sencilla", "floor": 1, "capacity": 1, "basePrice": 80.00, "description": "Habitación sencilla", "status": "AVAILABLE", "statusLabel": "Disponible" }]
```

### POST /api/hotel/reservar

Petición:

```json
{ "roomId": 7, "checkIn": "2026-11-01", "checkOut": "2026-11-04",
  "guest": { "fullName": "Ana Pérez", "documentNumber": "1020304050", "email": "ana@correo.com", "phone": "3001234567" } }
```

Validaciones: todos los datos del huésped son obligatorios y el correo debe ser válido. `checkIn` no puede estar en el pasado y `checkOut` debe ser posterior a `checkIn`.

Respuesta `201`: `ReservationDTO`.

```json
{
  "id": "RES-1001", "status": "CONFIRMED", "statusLabel": "Confirmada",
  "guest": { "fullName": "Ana Pérez", "documentNumber": "1020304050", "email": "ana@correo.com", "phone": "3001234567" },
  "room": { "id": 7, "number": "201", "type": "DOUBLE", "typeLabel": "Doble", "floor": 2, "capacity": 2, "basePrice": 120.00, "description": "Habitación doble con vista a la ciudad", "status": "AVAILABLE", "statusLabel": "Disponible" },
  "checkIn": "2026-11-01", "checkOut": "2026-11-04", "nights": 3,
  "roomTotal": 360.00,
  "services": [],
  "servicesTotal": 0.00, "estimatedTotal": 360.00,
  "digitalKey": null, "invoiceNumber": null,
  "createdAt": "2026-10-16T15:04:05", "checkedInAt": null, "checkedOutAt": null
}
```

### GET /api/hotel/reserva/{reservaId}

Respuesta `200`: `ReservationDTO`. Error `404` si la reserva no existe.

Ejemplo: `GET /api/hotel/reserva/RES-1001`

### GET /api/hotel/reservas

Respuesta `200`: arreglo de `ReservationDTO`, de la más reciente a la más antigua.
