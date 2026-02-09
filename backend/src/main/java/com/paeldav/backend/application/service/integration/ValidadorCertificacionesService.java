package com.paeldav.backend.application.service.integration;

import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.exception.CertificacionVencidaException;
import com.paeldav.backend.exception.RequisitoTecnicoNoMetException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Servicio de validación de certificaciones y requisitos técnicos de tripulantes.
 */
@Service
public class ValidadorCertificacionesService {

    /**
     * Valida que la licencia del tripulante no esté vencida.
     *
     * @param tripulante el tripulante a validar
     * @throws CertificacionVencidaException si la licencia está vencida
     */
    public void validarLicenciaVigente(Tripulante tripulante) {
        if (tripulante.getFechaVencimientoLicencia() != null &&
            tripulante.getFechaVencimientoLicencia().isBefore(LocalDate.now().plusDays(1))) {
            throw new CertificacionVencidaException(
                "La licencia de piloto " + tripulante.getNumeroLicencia() + " está vencida. " +
                "Vencimiento: " + tripulante.getFechaVencimientoLicencia()
            );
        }
    }

    /**
     * Valida los requisitos técnicos mínimos para un piloto.
     *
     * @param tripulante el tripulante a validar
     * @throws RequisitoTecnicoNoMetException si no cumple con los requisitos mínimos
     */
    public void validarRequisitosPiloto(Tripulante tripulante) {
        if (!tripulante.getEsPiloto()) {
            return; // No es piloto, no validamos requisitos de piloto
        }

        // Validar horas mínimas de vuelo (1000 horas)
        if (tripulante.getHorasVueloTotales() == null || tripulante.getHorasVueloTotales() < 1000) {
            throw new RequisitoTecnicoNoMetException(
                "El piloto " + tripulante.getNumeroLicencia() +
                " no cumple con las horas mínimas de vuelo (1000 horas). " +
                "Horas actuales: " + (tripulante.getHorasVueloTotales() != null ? tripulante.getHorasVueloTotales() : 0)
            );
        }

        // Validar que tenga certificaciones registradas
        if (tripulante.getCertificaciones() == null || tripulante.getCertificaciones().isEmpty()) {
            throw new RequisitoTecnicoNoMetException(
                "El piloto " + tripulante.getNumeroLicencia() +
                " no tiene certificaciones registradas"
            );
        }
    }

    /**
     * Valida los requisitos técnicos mínimos para un auxiliar de vuelo.
     *
     * @param tripulante el tripulante a validar
     * @throws RequisitoTecnicoNoMetException si no cumple con los requisitos mínimos
     */
    public void validarRequisitosAuxiliar(Tripulante tripulante) {
        if (tripulante.getEsPiloto()) {
            return; // Es piloto, no validamos requisitos de auxiliar
        }

        // Validar que tenga certificaciones de seguridad (mínimo)
        if (tripulante.getCertificaciones() == null || tripulante.getCertificaciones().isEmpty()) {
            throw new RequisitoTecnicoNoMetException(
                "El auxiliar de vuelo " + tripulante.getNumeroLicencia() +
                " no tiene certificaciones de seguridad registradas"
            );
        }
    }

    /**
     * Validación completa de un tripulante.
     *
     * @param tripulante el tripulante a validar
     * @throws CertificacionVencidaException si la licencia está vencida
     * @throws RequisitoTecnicoNoMetException si no cumple con los requisitos técnicos
     */
    public void validarTripulanteCompleto(Tripulante tripulante) {
        validarLicenciaVigente(tripulante);

        if (tripulante.getEsPiloto()) {
            validarRequisitosPiloto(tripulante);
        } else {
            validarRequisitosAuxiliar(tripulante);
        }
    }

    /**
     * Verifica si un tripulante está habilitado para volar.
     *
     * @param tripulante el tripulante a verificar
     * @return true si está habilitado, false en caso contrario
     */
    public boolean esValidoParaVolar(Tripulante tripulante) {
        try {
            validarTripulanteCompleto(tripulante);
            return true;
        } catch (CertificacionVencidaException | RequisitoTecnicoNoMetException e) {
            return false;
        }
    }

    /**
     * Valida que las horas de vuelo mensuales no excedan el límite reglamentario (100 horas).
     *
     * @param tripulante el tripulante a validar
     * @throws RequisitoTecnicoNoMetException si excede el límite mensual
     */
    public void validarHorasVueloMensuales(Tripulante tripulante) {
        if (tripulante.getHorasVueloMes() != null && tripulante.getHorasVueloMes() > 100) {
            throw new RequisitoTecnicoNoMetException(
                "El tripulante " + tripulante.getNumeroLicencia() +
                " ha excedido el límite de horas de vuelo mensuales (100 horas). " +
                "Horas acumuladas este mes: " + tripulante.getHorasVueloMes()
            );
        }
    }
}
