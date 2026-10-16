# Reglas de negocio

## Temporadas

| Temporada | Meses | Multiplicador | Etiqueta |
|---|---|---|---|
| Alta (`HIGH`) | enero, junio, julio, diciembre | 1.5 | Temporada alta |
| Baja (`LOW`) | resto del año | 1.0 | Temporada baja |

La temporada se determina por el mes de cada noche, no por la reserva completa. Una estadía que cruza de diciembre a enero se cobra por noche, con cada noche en su temporada.

## Precios

- El precio se calcula **por noche**. La noche de un día `D` cuenta desde el check-in (incluido) hasta el check-out (excluido).
- Precio de la noche = precio base del tipo de habitación x multiplicador de la temporada.
- Precio total de habitación = suma de los precios de todas las noches, con escala 2 y redondeo `HALF_UP`.

| Tipo | Precio base | Capacidad |
|---|---|---|
| Sencilla (`SINGLE`) | 80.00 | 1 |
| Doble (`DOUBLE`) | 120.00 | 2 |
| Suite (`SUITE`) | 250.00 | 4 |

Ejemplo: una habitación doble con check-in el 30 de diciembre y check-out el 2 de enero (3 noches, todas de temporada alta) cuesta 3 x 180.00 = 540.00.

## Servicios adicionales

| Servicio | Precio unitario | Modo de cobro | Cantidad |
|---|---|---|---|
| Spa (`SPA`) | 50.00 | Fijo (`FLAT`) | 1 |
| Desayuno (`BREAKFAST`) | 15.00 | Por noche (`PER_NIGHT`) | número de noches |
| Traslado (`TRANSFER`) | 30.00 | Fijo (`FLAT`) | 1 |

Un servicio solo se puede agregar cuando la reserva está `CONFIRMED` o `CHECKED_IN`.

## Impuestos (IVA)

- IVA de 19% (`taxRate` 0.19).
- Base del impuesto: subtotal = total de habitación + total de servicios.
- `taxAmount = subtotal x 0.19`, con redondeo `HALF_UP` a 2 decimales.
- `total = subtotal + taxAmount`.

Ejemplo: subtotal 405.00, IVA 76.95, total 481.95.

## Disponibilidad

Una habitación no está disponible si tiene una reserva en estado `CONFIRMED` o `CHECKED_IN` cuyo rango se traslapa con el solicitado. El rango es semiabierto `[checkIn, checkOut)`, así que una reserva que sale el día 4 no impide entrar el día 4.

## Estados de la reserva

```
CONFIRMED --check-in--> CHECKED_IN --check-out--> CHECKED_OUT
```

- Check-in solo desde `CONFIRMED`. Genera la llave digital y marca la habitación como `OCCUPIED`.
- Check-out solo desde `CHECKED_IN`. Genera la factura, revoca la llave (`active = false`), libera la habitación (`AVAILABLE`) y elimina su reserva de la habitación.

Una transición inválida devuelve HTTP 409.

## Facturación

Las líneas de la factura son:

- **Habitación** (`ROOM`): una línea por temporada presente. Las noches de temporada alta se agrupan con precio unitario base x 1.5, y las de temporada baja con precio base.
- **Servicios** (`SERVICE`): una línea por cada servicio agregado.

El número de factura tiene el formato `FAC-0001`, `FAC-0002`, ...
