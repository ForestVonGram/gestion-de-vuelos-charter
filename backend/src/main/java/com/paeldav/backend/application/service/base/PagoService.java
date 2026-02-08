package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.pago.PagoCreateDTO;
import com.paeldav.backend.application.dto.pago.PagoDTO;
import com.paeldav.backend.domain.enums.EstadoPago;

import java.util.List;

/**
 * Interfaz de servicio para gestionar pagos de vuelos.
 * Define las operaciones disponibles para crear, actualizar y consultar pagos.
 */
public interface PagoService {

    /**
     * Inicia un nuevo pago para un vuelo.
     * Crea el registro de pago en estado PENDIENTE y genera la preferencia en MercadoPago.
     *
     * @param pagoCreateDTO DTO con los datos del pago
     * @return PagoDTO con los datos del pago creado incluida la URL de pago
     */
    PagoDTO iniciarPago(PagoCreateDTO pagoCreateDTO);

    /**
     * Obtiene un pago por su ID.
     *
     * @param id ID del pago
     * @return PagoDTO con los datos del pago
     */
    PagoDTO obtenerPagoPorId(Long id);

    /**
     * Obtiene todos los pagos de un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return lista de pagos del vuelo
     */
    List<PagoDTO> obtenerPagosPorVuelo(Long vueloId);

    /**
     * Obtiene todos los pagos de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return lista de pagos del usuario
     */
    List<PagoDTO> obtenerPagosPorUsuario(Long usuarioId);

    /**
     * Obtiene todos los pagos en un estado específico.
     *
     * @param estado estado del pago
     * @return lista de pagos con el estado especificado
     */
    List<PagoDTO> obtenerPagosPorEstado(EstadoPago estado);

    /**
     * Confirma un pago que ha sido exitoso en MercadoPago.
     *
     * @param pagoId ID del pago
     * @param referenciaMercadoPago referencia/ID del pago en MercadoPago
     * @return PagoDTO con el estado actualizado a CONFIRMADO
     */
    PagoDTO confirmarPago(Long pagoId, String referenciaMercadoPago);

    /**
     * Rechaza un pago que ha fallado.
     *
     * @param pagoId ID del pago
     * @param motivo motivo del rechazo
     * @return PagoDTO con el estado actualizado a RECHAZADO
     */
    PagoDTO rechazarPago(Long pagoId, String motivo);

    /**
     * Procesa un webhook de MercadoPago.
     * Actualiza el estado del pago basado en la notificación recibida.
     *
     * @param referenciaMercadoPago referencia del pago en MercadoPago
     * @param estado estado del pago según MercadoPago
     */
    void procesarWebhook(String referenciaMercadoPago, String estado);

    /**
     * Obtiene el total de pagos confirmados para un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return total en moneda
     */
    Double obtenerTotalPagosConfirmados(Long vueloId);

    /**
     * Verifica si un vuelo tiene todos sus pagos confirmados.
     *
     * @param vueloId ID del vuelo
     * @param montoRequerido monto total requerido
     * @return true si hay pagos confirmados que cubren el monto, false en caso contrario
     */
    boolean tienePagoConfirmado(Long vueloId, Double montoRequerido);

    /**
     * Reembolsa un pago confirmado.
     *
     * @param pagoId ID del pago a reembolsar
     * @param motivo motivo del reembolso
     * @return PagoDTO con el estado actualizado a REEMBOLSADO
     */
    PagoDTO reembolsarPago(Long pagoId, String motivo);
}
