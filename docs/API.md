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
