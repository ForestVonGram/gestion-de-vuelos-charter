package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.auth.Verificacion2FAResponse;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.VerificacionDosFactores;
import com.paeldav.backend.domain.enums.MetodoDosFactores;

public interface DosFactoresService {

    /**
     * Genera un nuevo código de verificación 2FA para un usuario.
     */
    VerificacionDosFactores generarCodigoVerificacion(Usuario usuario, MetodoDosFactores metodo, String destino);

    /**
     * Verifica un código de 2FA.
     */
    VerificacionDosFactores verificarCodigo(String codigo);

    /**
     * Incrementa el contador de intentos fallidos.
     */
    void incrementarIntentosFallidos(VerificacionDosFactores verificacion);

    /**
     * Marca una verificación como completada.
     */
    void marcarComoVerificado(VerificacionDosFactores verificacion);

    /**
     * Habilita 2FA para un usuario.
     */
    void habilitarDosFactores(Usuario usuario, MetodoDosFactores metodo, String destino);

    /**
     * Deshabilita 2FA para un usuario.
     */
    void deshabilitarDosFactores(Usuario usuario);

    /**
     * Verifica si 2FA está habilitado para un usuario.
     */
    boolean esActivo(Usuario usuario);

    /**
     * Obtiene información de la última verificación 2FA pendiente.
     */
    Verificacion2FAResponse obtenerInfoVerificacion(Usuario usuario);

    /**
     * Enmascare un email o teléfono para mostrar en respuesta.
     */
    String enmascaraDestino(String destino, MetodoDosFactores metodo);
}
