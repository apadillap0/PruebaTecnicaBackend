package com.alianza.pruebatecnica.cliente.utils;

public enum MensajeClienteEnum {

    ERROR201("Sr. Usuario, el sharedKey %s ya está registrado en el sistema"),
    ERROR202("Sr. Usuario, el email %s ya está registrado en el sistema"),
    ERROR203("Sr. Usuario, el cliente a actualizar no se encuentra en el sistema"),
    ERROR204("Sr. Usuario, el cliente con sharedKey %s no se encuentra en el sistema");

    private String description;

    private MensajeClienteEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getMensajeConParametros(String... params) {
        String mensaje = String.format(description, (Object[]) params);
        return mensaje;
    }
}
