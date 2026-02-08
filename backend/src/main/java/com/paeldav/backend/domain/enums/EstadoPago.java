package com.paeldav.backend.domain.enums;

/**
 * Estados posibles de un pago en el sistema.
 */
public enum EstadoPago {
    /**
     * El pago ha sido iniciado pero aún está pendiente de confirmación.
     */
    PENDIENTE,

    /**
     * El pago ha sido confirmado exitosamente por MercadoPago.
     */
    CONFIRMADO,

    /**
     * El pago fue rechazado o falló por alguna razón.
     */
    RECHAZADO,

    /**
     * El pago ha sido reembolsado al cliente.
     */
    REEMBOLSADO
}
