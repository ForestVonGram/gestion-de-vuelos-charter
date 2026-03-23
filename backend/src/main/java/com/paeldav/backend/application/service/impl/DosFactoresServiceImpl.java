package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.auth.Verificacion2FAResponse;
import com.paeldav.backend.application.service.base.DosFactoresService;
import com.paeldav.backend.application.service.integration.EmailServiceImpl;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.VerificacionDosFactores;
import com.paeldav.backend.domain.enums.MetodoDosFactores;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VerificacionDosFactoresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Implementación del servicio de autenticación de dos factores (2FA).
 * Gestiona generación, verificación y configuración de códigos de verificación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DosFactoresServiceImpl implements DosFactoresService {

    // Repositorio para persistencia de códigos de verificación 2FA
    private final VerificacionDosFactoresRepository verificacionRepository;

    // Repositorio para operaciones sobre usuarios
    private final UsuarioRepository usuarioRepository;

    // Servicio para envío de correos electrónicos
    private final EmailServiceImpl emailServiceImpl;

    // Tiempo de expiración del código 2FA (configurable desde properties)
    @Value("${2fa.code-expiration-minutes:10}")
    private int codigoExpiracionMinutos;

    // Número máximo de intentos fallidos permitidos
    @Value("${2fa.max-attempts:3}")
    private int maxIntentosFallidos;

    // Longitud del código de verificación
    private static final int CODIGO_LONGITUD = 6;

    /**
     * Genera un nuevo código de verificación 2FA para un usuario.
     */
    @Override
    @Transactional
    public VerificacionDosFactores generarCodigoVerificacion(Usuario usuario, MetodoDosFactores metodo, String destino) {

        // Invalidar códigos anteriores no verificados del usuario
        verificacionRepository.invalidateUnverifiedByUsuarioId(usuario.getId());

        // Generar código aleatorio
        String codigo = generarCodigoAleatorio();

        // Definir fechas de creación y expiración
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaExpiracion = ahora.plusMinutes(codigoExpiracionMinutos);

        // Crear nueva entidad de verificación
        VerificacionDosFactores verificacion = VerificacionDosFactores.builder()
                .usuario(usuario)
                .codigo(codigo)
                .metodo(metodo)
                .destino(destino)
                .fechaExpiracion(fechaExpiracion)
                .intentosFallidos(0)
                .verificado(false)
                .activo(true)
                .build();

        // Guardar verificación en base de datos
        verificacion = verificacionRepository.save(verificacion);

        // Enviar código por email si el método seleccionado es EMAIL
        if (MetodoDosFactores.EMAIL.equals(metodo)) {
            emailServiceImpl.enviarCodigoVerificacion2FA(destino, codigo, usuario.getNombre());
        }

        log.info("Codigo 2FA generado para usuario: {}", usuario.getId());

        return verificacion;
    }

    /**
     * Verifica si un código 2FA es válido y no ha expirado.
     */
    @Override
    @Transactional(readOnly = true)
    public VerificacionDosFactores verificarCodigo(String codigo) {
        return verificacionRepository.findValidCode(codigo)
                .orElseThrow(() -> new NoSuchElementException("Codigo de verificacion invalido o expirado"));
    }

    /**
     * Incrementa el número de intentos fallidos para un código de verificación.
     */
    @Override
    @Transactional
    public void incrementarIntentosFallidos(VerificacionDosFactores verificacion) {

        // Aumentar contador de intentos fallidos
        verificacion.setIntentosFallidos(verificacion.getIntentosFallidos() + 1);

        // Desactivar código si se excede el número máximo de intentos
        if (verificacion.excedioIntentos(maxIntentosFallidos)) {
            verificacion.setActivo(false);
        }

        verificacionRepository.save(verificacion);
    }

    /**
     * Marca un código 2FA como verificado.
     */
    @Override
    @Transactional
    public void marcarComoVerificado(VerificacionDosFactores verificacion) {

        verificacion.setVerificado(true);
        verificacion.setFechaVerificacion(LocalDateTime.now());

        verificacionRepository.save(verificacion);
    }

    /**
     * Habilita la autenticación de dos factores para un usuario.
     */
    @Override
    @Transactional
    public void habilitarDosFactores(Usuario usuario, MetodoDosFactores metodo, String destino) {

        usuario.setDosFactoresHabilitado(true);
        usuario.setMetodoDosFactores(metodo);

        usuarioRepository.save(usuario);
    }

    /**
     * Deshabilita la autenticación de dos factores para un usuario.
     */
    @Override
    @Transactional
    public void deshabilitarDosFactores(Usuario usuario) {

        usuario.setDosFactoresHabilitado(false);
        usuario.setMetodoDosFactores(null);

        usuarioRepository.save(usuario);
    }

    /**
     * Verifica si el usuario tiene 2FA activo.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean esActivo(Usuario usuario) {
        return usuario.getDosFactoresHabilitado() != null && usuario.getDosFactoresHabilitado();
    }

    /**
     * Obtiene información del último proceso de verificación 2FA del usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public Verificacion2FAResponse obtenerInfoVerificacion(Usuario usuario) {

        return verificacionRepository.findLatestByUsuarioId(usuario.getId())
                .map(v -> {

                    // Calcular tiempo restante de expiración en milisegundos
                    long tiempoRestanteMs = v.getFechaExpiracion().atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                            - LocalDateTime.now().atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli();

                    // Construir respuesta con información del proceso de verificación
                    return Verificacion2FAResponse.builder()
                            .metodo(v.getMetodo())
                            .destino(enmascaraDestino(v.getDestino(), v.getMetodo()))
                            .tiempoExpiracion((int) (tiempoRestanteMs / 1000))
                            .intentosRestantes(maxIntentosFallidos - v.getIntentosFallidos())
                            .build();
                })
                .orElse(null);
    }

    /**
     * Enmascara el destino del código (email o teléfono) para mostrarlo parcialmente.
     */
    @Override
    public String enmascaraDestino(String destino, MetodoDosFactores metodo) {

        if (MetodoDosFactores.EMAIL.equals(metodo)) {

            String[] partes = destino.split("@");

            if (partes.length == 2) {

                String usuario = partes[0];
                String dominio = partes[1];

                // Mostrar solo los primeros caracteres del usuario
                String usuarioMascarado = usuario.substring(0, Math.min(2, usuario.length())) + "****";

                return usuarioMascarado + "@" + dominio;
            }

        } else if (MetodoDosFactores.SMS.equals(metodo)) {

            if (destino.length() >= 4) {

                // Mostrar solo los últimos 4 dígitos del número
                return "*****" + destino.substring(destino.length() - 4);
            }
        }

        return destino;
    }

    /**
     * Genera un código aleatorio de 6 dígitos para verificación 2FA.
     */
    private String generarCodigoAleatorio() {

        SecureRandom random = new SecureRandom();

        int codigo = random.nextInt(1000000);

        // Formatear el número para asegurar 6 dígitos
        return String.format("%0" + CODIGO_LONGITUD + "d", codigo);
    }
}