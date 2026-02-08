package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.pago.PagoCreateDTO;
import com.paeldav.backend.application.dto.pago.PagoDTO;
import com.paeldav.backend.application.mapper.PagoMapper;
import com.paeldav.backend.application.service.base.PagoService;
import com.paeldav.backend.application.service.integration.MercadoPagoService;
import com.paeldav.backend.domain.entity.Pago;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoPago;
import com.paeldav.backend.exception.PagoNoEncontradoException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.PagoRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de gestión de pagos.
 * Maneja la lógica de negocio para crear, confirmar y consultar pagos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final VueloRepository vueloRepository;
    private final UsuarioRepository usuarioRepository;
    private final MercadoPagoService mercadoPagoService;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional
    public PagoDTO iniciarPago(PagoCreateDTO pagoCreateDTO) {
        log.info("Iniciando pago para vuelo ID: {}", pagoCreateDTO.getVueloId());

        // Validar que el vuelo existe
        Vuelo vuelo = vueloRepository.findById(pagoCreateDTO.getVueloId())
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + pagoCreateDTO.getVueloId()
                ));

        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(pagoCreateDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + pagoCreateDTO.getUsuarioId()
                ));

        // Crear preferencia en MercadoPago
        MercadoPagoService.PreferenciaResponse preferencia = 
                mercadoPagoService.crearPreferencia(
                        pagoCreateDTO.getVueloId(),
                        pagoCreateDTO.getMonto(),
                        pagoCreateDTO.getEmailCliente(),
                        pagoCreateDTO.getDescripcion()
                );

        // Crear registro de pago
        Pago pago = Pago.builder()
                .vuelo(vuelo)
                .usuario(usuario)
                .monto(pagoCreateDTO.getMonto())
                .estado(EstadoPago.PENDIENTE)
                .numeroPreferencia(preferencia.getNumeroPreferencia())
                .emailCliente(pagoCreateDTO.getEmailCliente())
                .observaciones("Pago iniciado - Preferencia: " + preferencia.getNumeroPreferencia())
                .build();

        pago = pagoRepository.save(pago);
        log.info("Pago creado exitosamente. ID: {}, Número Preferencia: {}", 
                 pago.getId(), preferencia.getNumeroPreferencia());

        PagoDTO pagoDTO = pagoMapper.toDTO(pago);
        pagoDTO.setUrlPago(preferencia.getUrlPago());

        return pagoDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public PagoDTO obtenerPagoPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNoEncontradoException(
                        "Pago no encontrado con ID: " + id
                ));

        return pagoMapper.toDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> obtenerPagosPorVuelo(Long vueloId) {
        List<Pago> pagos = pagoRepository.findByVueloId(vueloId);
        return pagoMapper.toDTOList(pagos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> obtenerPagosPorUsuario(Long usuarioId) {
        List<Pago> pagos = pagoRepository.findByUsuarioId(usuarioId);
        return pagoMapper.toDTOList(pagos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> obtenerPagosPorEstado(EstadoPago estado) {
        List<Pago> pagos = pagoRepository.findByEstado(estado);
        return pagoMapper.toDTOList(pagos);
    }

    @Override
    @Transactional
    public PagoDTO confirmarPago(Long pagoId, String referenciaMercadoPago) {
        log.info("Confirmando pago ID: {} con referencia MercadoPago: {}", pagoId, referenciaMercadoPago);

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException(
                        "Pago no encontrado con ID: " + pagoId
                ));

        // Validar que no esté en estado final
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new IllegalStateException(
                    "No se puede confirmar un pago en estado: " + pago.getEstado()
            );
        }

        // Actualizar estado
        pago.setEstado(EstadoPago.CONFIRMADO);
        pago.setReferenciaMercadoPago(referenciaMercadoPago);
        pago.setFechaPago(LocalDateTime.now());
        pago.setObservaciones("Pago confirmado. Referencia MP: " + referenciaMercadoPago);

        pago = pagoRepository.save(pago);
        log.info("Pago confirmado exitosamente. ID: {}", pagoId);

        return pagoMapper.toDTO(pago);
    }

    @Override
    @Transactional
    public PagoDTO rechazarPago(Long pagoId, String motivo) {
        log.info("Rechazando pago ID: {} - Motivo: {}", pagoId, motivo);

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException(
                        "Pago no encontrado con ID: " + pagoId
                ));

        // Validar que no esté en estado final
        if (pago.getEstado() != EstadoPago.PENDIENTE && pago.getEstado() != EstadoPago.CONFIRMADO) {
            throw new IllegalStateException(
                    "No se puede rechazar un pago en estado: " + pago.getEstado()
            );
        }

        pago.setEstado(EstadoPago.RECHAZADO);
        pago.setObservaciones("Pago rechazado. Motivo: " + motivo);

        pago = pagoRepository.save(pago);
        log.info("Pago rechazado. ID: {}", pagoId);

        return pagoMapper.toDTO(pago);
    }

    @Override
    @Transactional
    public void procesarWebhook(String referenciaMercadoPago, String estado) {
        log.info("Procesando webhook de MercadoPago. Referencia: {}, Estado: {}", 
                 referenciaMercadoPago, estado);

        // Buscar pago por referencia de MercadoPago
        pagoRepository.findByReferenciaMercadoPago(referenciaMercadoPago)
                .ifPresentOrElse(
                        pago -> {
                            if ("approved".equalsIgnoreCase(estado)) {
                                pago.setEstado(EstadoPago.CONFIRMADO);
                                pago.setFechaPago(LocalDateTime.now());
                            } else if ("rejected".equalsIgnoreCase(estado) || 
                                     "cancelled".equalsIgnoreCase(estado)) {
                                pago.setEstado(EstadoPago.RECHAZADO);
                            }

                            pagoRepository.save(pago);
                            log.info("Webhook procesado para pago ID: {}", pago.getId());
                        },
                        () -> log.warn("Pago no encontrado para referencia: {}", referenciaMercadoPago)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalPagosConfirmados(Long vueloId) {
        Double total = pagoRepository.getTotalConfirmedAmountForFlight(vueloId);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tienePagoConfirmado(Long vueloId, Double montoRequerido) {
        Double totalConfirmado = obtenerTotalPagosConfirmados(vueloId);
        boolean resultado = totalConfirmado >= montoRequerido;
        log.debug("Verificación de pago confirmado - Vuelo: {}, Requerido: {}, Confirmado: {}, Resultado: {}",
                 vueloId, montoRequerido, totalConfirmado, resultado);
        return resultado;
    }

    /**
     * Reembolsa un pago confirmado.
     * @param pagoId ID del pago a reembolsar
     * @param motivo motivo del reembolso
     * @return PagoDTO con el estado actualizado a REEMBOLSADO
     */
    @Transactional
    public PagoDTO reembolsarPago(Long pagoId, String motivo) {
        log.info("Iniciando reembolso para pago ID: {}", pagoId);

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException(
                        "Pago no encontrado con ID: " + pagoId
                ));

        // Validar que el pago esté en estado CONFIRMADO
        if (pago.getEstado() != EstadoPago.CONFIRMADO) {
            throw new IllegalStateException(
                    "Solo se pueden reembolsar pagos en estado CONFIRMADO. Estado actual: " + pago.getEstado()
            );
        }

        // Intentar procesar reembolso en MercadoPago
        try {
            // Si tenemos referencia de MP, procesamos el reembolso allá
            if (pago.getReferenciaMercadoPago() != null) {
                MercadoPagoService.RefundResponse refund = 
                        mercadoPagoService.reembolsarPago(
                                Long.parseLong(pago.getReferenciaMercadoPago()), 
                                pago.getMonto()
                        );
                log.info("Reembolso procesado en MercadoPago. RefundId: {}", refund.getRefundId());
            }
        } catch (Exception e) {
            log.error("Error procesando reembolso en MercadoPago: {}", e.getMessage());
            // Continuamos con el reembolso local aunque MercadoPago falle
        }

        // Actualizar estado del pago
        pago.setEstado(EstadoPago.REEMBOLSADO);
        pago.setFechaPago(LocalDateTime.now());
        pago.setObservaciones("Pago reembolsado. Motivo: " + motivo);

        pago = pagoRepository.save(pago);
        log.info("Pago reembolsado exitosamente. ID: {}", pagoId);

        return pagoMapper.toDTO(pago);
    }
}
