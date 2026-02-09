package com.paeldav.backend.domain.enums;

public enum MetodoDosFactores {
    EMAIL("Por correo electronico"),
    SMS("Por mensaje de texto");

    private final String descripcion;

    MetodoDosFactores(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
