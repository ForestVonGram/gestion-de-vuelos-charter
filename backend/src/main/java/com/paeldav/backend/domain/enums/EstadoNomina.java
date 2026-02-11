package com.paeldav.backend.domain.enums;

/**
 * Enum que representa los posibles estados de una nómina.
 */
public enum EstadoNomina {
    /** La nómina ha sido generada pero aún no procesada */
    PENDIENTE,
    /** La nómina ha sido pagada exitosamente */
    PAGADA,
    /** La nómina ha sido retenida o cancelada */
    RETENIDA,
    /** La nómina está en proceso de pago */
    EN_PROCESO
}
