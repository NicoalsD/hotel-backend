package com.hotel.reservations.model;

/**
 * Huésped que realiza una reserva.
 */
public class Guest {

    private final String fullName;
    private final String documentNumber;
    private final String email;
    private final String phone;

    /**
     * @param fullName nombre completo del huésped
     * @param documentNumber número de documento de identidad
     * @param email correo electrónico de contacto
     * @param phone teléfono de contacto
     */
    public Guest(String fullName, String documentNumber, String email, String phone) {
        this.fullName = fullName;
        this.documentNumber = documentNumber;
        this.email = email;
        this.phone = phone;
    }

    /** @return nombre completo */
    public String getFullName() {
        return fullName;
    }

    /** @return número de documento */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /** @return correo electrónico */
    public String getEmail() {
        return email;
    }

    /** @return teléfono de contacto */
    public String getPhone() {
        return phone;
    }
}
