package com.paeldav.backend.application.service.base;

/**
 * Servicio para validar si una aeronave es operativa.
 * Verifica restricciones basadas en mantenimientos pendientes y vencidos.
 */
public interface ValidadorOperativoService {

    /**
     * Valida si una aeronave puede despegar basado en su estado de mantenimiento.
     *
     * @param aeronaveId identificador de la aeronave
     * @return true si la aeronave es operativa, false si no puede despegar
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si la aeronave no existe
     */
    boolean esAeronaveOperativa(Long aeronaveId);

    /**
     * Obtiene la razón por la cual una aeronave no es operativa.
     *
     * @param aeronaveId identificador de la aeronave
     * @return mensaje explicativo de la restricción, o null si es operativa
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si la aeronave no existe
     */
    String obtenerRazonNoOperativa(Long aeronaveId);

    /**
     * Valida si una aeronave tiene mantenimiento pendiente vencido.
     *
     * @param aeronaveId identificador de la aeronave
     * @return true si tiene mantenimiento vencido, false en caso contrario
     */
    boolean tieneMantenimientoVencido(Long aeronaveId);

    /**
     * Valida si una aeronave tiene mantenimiento pendiente (no necesariamente vencido).
     *
     * @param aeronaveId identificador de la aeronave
     * @return true si tiene mantenimiento pendiente, false en caso contrario
     */
    boolean tieneMantenimientoPendiente(Long aeronaveId);

    /**
     * Obtiene un resumen de la operatividad de la aeronave con detalles.
     *
     * @param aeronaveId identificador de la aeronave
     * @return objeto con información sobre la operatividad
     */
    ResumenOperatividad obtenerResumenOperatividad(Long aeronaveId);

    /**
     * Clase interna para representar el resumen de operatividad.
     */
    class ResumenOperatividad {
        public boolean esOperativa;
        public String razon;
        public boolean tieneMantenimientoVencido;
        public boolean tieneMantenimientoPendiente;
        public int cantidadMantenimientosVencidos;
        public int cantidadMantenimientosPendientes;

        public ResumenOperatividad(boolean esOperativa, String razon, 
                                    boolean tieneMantenimientoVencido,
                                    boolean tieneMantenimientoPendiente,
                                    int cantidadMantenimientosVencidos,
                                    int cantidadMantenimientosPendientes) {
            this.esOperativa = esOperativa;
            this.razon = razon;
            this.tieneMantenimientoVencido = tieneMantenimientoVencido;
            this.tieneMantenimientoPendiente = tieneMantenimientoPendiente;
            this.cantidadMantenimientosVencidos = cantidadMantenimientosVencidos;
            this.cantidadMantenimientosPendientes = cantidadMantenimientosPendientes;
        }
    }
}
