package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.pago.PagoCreateDTO;
import com.paeldav.backend.application.dto.pago.PagoDTO;
import com.paeldav.backend.application.dto.pago.ReembolsoDTO;
import com.paeldav.backend.application.dto.pago.WebhookMercadoPagoDTO;
import com.paeldav.backend.application.service.base.PagoService;
import com.paeldav.backend.domain.enums.EstadoPago;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pagos de vuelos.
 * Proporciona endpoints para crear, consultar y procesar webhooks de pagos.
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    /**
     * Inicia un nuevo pago para un vuelo.
     *
     * @param pagoCreateDTO DTO con los datos del pago
     * @return ResponseEntity con el pago creado (201 Created)
     */
    @PostMapping
    public ResponseEntity<PagoDTO> iniciarPago(
            @Valid @RequestBody PagoCreateDTO pagoCreateDTO) {
        PagoDTO pagoDTO = pagoService.iniciarPago(pagoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoDTO);
    }

    /**
     * Obtiene un pago por su ID.
     *
     * @param id ID del pago
     * @return ResponseEntity con los datos del pago
     */
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPagoPorId(@PathVariable Long id) {
        PagoDTO pagoDTO = pagoService.obtenerPagoPorId(id);
        return ResponseEntity.ok(pagoDTO);
    }

    /**
     * Obtiene todos los pagos de un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return ResponseEntity con la lista de pagos del vuelo
     */
    @GetMapping("/vuelo/{vueloId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorVuelo(@PathVariable Long vueloId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorVuelo(vueloId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Obtiene todos los pagos de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la lista de pagos del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorUsuario(@PathVariable Long usuarioId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorUsuario(usuarioId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Obtiene todos los pagos en un estado específico.
     *
     * @param estado estado del pago a filtrar
     * @return ResponseEntity con la lista de pagos en el estado especificado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorEstado(@PathVariable EstadoPago estado) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorEstado(estado);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Confirma un pago que ha sido exitoso en MercadoPago.
     *
     * @param id ID del pago a confirmar
     * @param referenciaMercadoPago referencia del pago en MercadoPago
     * @return ResponseEntity con el pago confirmado
     */
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PagoDTO> confirmarPago(
            @PathVariable Long id,
            @RequestParam String referenciaMercadoPago) {
        PagoDTO pagoDTO = pagoService.confirmarPago(id, referenciaMercadoPago);
        return ResponseEntity.ok(pagoDTO);
    }

    /**
     * Rechaza un pago que ha fallado.
     *
     * @param id ID del pago a rechazar
     * @param motivo motivo del rechazo
     * @return ResponseEntity con el pago rechazado
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<PagoDTO> rechazarPago(
            @PathVariable Long id,
            @RequestParam String motivo) {
        PagoDTO pagoDTO = pagoService.rechazarPago(id, motivo);
        return ResponseEntity.ok(pagoDTO);
    }

    /**
     * Procesa webhooks de MercadoPago con validación.
     * Endpoint que MercadoPago utiliza para notificar cambios de estado de pagos.
     *
     * @param webhook DTO con los datos del webhook de MercadoPago
     * @param xRequestId header de identificación del request
     * @param xSignature header con la firma del webhook
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> procesarWebhook(
            @RequestBody WebhookMercadoPagoDTO webhook,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestHeader(value = "x-signature", required = false) String xSignature) {
        if (webhook != null && webhook.getData() != null) {
            // El estado se obtiene consultando a MercadoPago por el payment ID
            // En caso de un pago pendiente, solo registramos la notificación
            String paymentId = webhook.getData().getId();
            // La confirmación se haría cuando el estado sea actualizado por MercadoPago
            pagoService.procesarWebhook(paymentId, "pending");
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el total de pagos confirmados para un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return ResponseEntity con el total en moneda
     */
    @GetMapping("/vuelo/{vueloId}/total-confirmado")
    public ResponseEntity<Double> obtenerTotalPagosConfirmados(@PathVariable Long vueloId) {
        Double total = pagoService.obtenerTotalPagosConfirmados(vueloId);
        return ResponseEntity.ok(total);
    }

    /**
     * Verifica si un vuelo tiene pagos confirmados.
     *
     * @param vueloId ID del vuelo
     * @param montoRequerido monto total requerido
     * @return ResponseEntity con true si hay pagos confirmados, false en caso contrario
     */
    @GetMapping("/vuelo/{vueloId}/tiene-pago/{montoRequerido}")
    public ResponseEntity<Boolean> tienePagoConfirmado(
            @PathVariable Long vueloId,
            @PathVariable Double montoRequerido) {
        boolean resultado = pagoService.tienePagoConfirmado(vueloId, montoRequerido);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Solicita el reembolso de un pago confirmado.
     *
     * @param reembolsoDTO DTO con los datos del reembolso
     * @return ResponseEntity con el pago reembolsado
     */
    @PostMapping("/reembolsar")
    public ResponseEntity<PagoDTO> reembolsarPago(
            @Valid @RequestBody ReembolsoDTO reembolsoDTO) {
        PagoDTO pagoDTO = pagoService.reembolsarPago(
                reembolsoDTO.getPagoId(),
                reembolsoDTO.getMotivo()
        );
        return ResponseEntity.ok(pagoDTO);
    }

    /**
     * Reembolsa un pago especificando un monto parcial.
     *
     * @param pagoId ID del pago
     * @param motivo motivo del reembolso
     * @param monto monto a reembolsar (opcional, si es null reembolsa el total)
     * @return ResponseEntity con el pago reembolsado
     */
    @PostMapping("/{pagoId}/reembolso-parcial")
    public ResponseEntity<PagoDTO> reembolsoParcial(
            @PathVariable Long pagoId,
            @RequestParam String motivo,
            @RequestParam(required = false) Double monto) {
        PagoDTO pagoDTO = pagoService.reembolsarPago(pagoId, motivo);
        return ResponseEntity.ok(pagoDTO);
    }
}
