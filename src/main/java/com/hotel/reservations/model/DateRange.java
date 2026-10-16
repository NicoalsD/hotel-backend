package com.hotel.reservations.model;

import com.hotel.reservations.exception.InvalidDateRangeException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rango de fechas inmutable de una estancia. Usa intervalos semiabiertos [checkIn, checkOut):
 * la fecha de salida no cuenta como noche ocupada.
 */
public final class DateRange {

    private final LocalDate checkIn;
    private final LocalDate checkOut;

    /**
     * @param checkIn fecha de entrada (obligatoria)
     * @param checkOut fecha de salida (obligatoria, posterior a la entrada)
     * @throws InvalidDateRangeException si alguna fecha es nula o la salida no es posterior a la entrada
     */
    public DateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new InvalidDateRangeException("Las fechas de entrada y salida son obligatorias");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidDateRangeException(
                    "La fecha de salida debe ser posterior a la fecha de entrada");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    /** @return fecha de entrada */
    public LocalDate getCheckIn() {
        return checkIn;
    }

    /** @return fecha de salida */
    public LocalDate getCheckOut() {
        return checkOut;
    }

    /** @return número de noches del rango */
    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Indica si dos rangos comparten al menos una noche.
     *
     * @param other rango a comparar
     * @return {@code true} si los intervalos semiabiertos se solapan
     */
    public boolean overlaps(DateRange other) {
        return checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(checkOut);
    }

    /** @return la fecha de cada noche, desde la entrada (inclusive) hasta la salida (exclusive) */
    public List<LocalDate> getNightDates() {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            dates.add(date);
        }
        return dates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateRange)) {
            return false;
        }
        DateRange that = (DateRange) o;
        return checkIn.equals(that.checkIn) && checkOut.equals(that.checkOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut);
    }

    @Override
    public String toString() {
        return checkIn + "/" + checkOut;
    }
}
