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

@Service
@RequiredArgsConstructor
@Slf4j
public class DosFactoresServiceImpl implements DosFactoresService {

    private final VerificacionDosFactoresRepository verificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailServiceImpl emailServiceImpl;

    @Value("${2fa.code-expiration-minutes:10}")
    private int codigoExpiracionMinutos;

    @Value("${2fa.max-attempts:3}")
    private int maxIntentosFallidos;

    private static final int CODIGO_LONGITUD = 6;

    @Override
    @Transactional
    public VerificacionDosFactores generarCodigoVerificacion(Usuario usuario, MetodoDosFactores metodo, String destino) {
        verificacionRepository.invalidateUnverifiedByUsuarioId(usuario.getId());
        String codigo = generarCodigoAleatorio();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaExpiracion = ahora.plusMinutes(codigoExpiracionMinutos);

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

        verificacion = verificacionRepository.save(verificacion);

        if (MetodoDosFactores.EMAIL.equals(metodo)) {
            emailServiceImpl.enviarCodigoVerificacion2FA(destino, codigo, usuario.getNombre());
        }

        log.info("Codigo 2FA generado para usuario: {}", usuario.getId());
        return verificacion;
    }

    @Override
    @Transactional(readOnly = true)
    public VerificacionDosFactores verificarCodigo(String codigo) {
        return verificacionRepository.findValidCode(codigo)
                .orElseThrow(() -> new NoSuchElementException("Codigo de verificacion invalido o expirado"));
    }

    @Override
    @Transactional
    public void incrementarIntentosFallidos(VerificacionDosFactores verificacion) {
        verificacion.setIntentosFallidos(verificacion.getIntentosFallidos() + 1);
        if (verificacion.excedioIntentos(maxIntentosFallidos)) {
            verificacion.setActivo(false);
        }
        verificacionRepository.save(verificacion);
    }

    @Override
    @Transactional
    public void marcarComoVerificado(VerificacionDosFactores verificacion) {
        verificacion.setVerificado(true);
        verificacion.setFechaVerificacion(LocalDateTime.now());
        verificacionRepository.save(verificacion);
    }

    @Override
    @Transactional
    public void habilitarDosFactores(Usuario usuario, MetodoDosFactores metodo, String destino) {
        usuario.setDosFactoresHabilitado(true);
        usuario.setMetodoDosFactores(metodo);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deshabilitarDosFactores(Usuario usuario) {
        usuario.setDosFactoresHabilitado(false);
        usuario.setMetodoDosFactores(null);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esActivo(Usuario usuario) {
        return usuario.getDosFactoresHabilitado() != null && usuario.getDosFactoresHabilitado();
    }

    @Override
    @Transactional(readOnly = true)
    public Verificacion2FAResponse obtenerInfoVerificacion(Usuario usuario) {
        return verificacionRepository.findLatestByUsuarioId(usuario.getId())
                .map(v -> {
                    long tiempoRestanteMs = v.getFechaExpiracion().atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                            - LocalDateTime.now().atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli();

                    return Verificacion2FAResponse.builder()
                            .metodo(v.getMetodo())
                            .destino(enmascaraDestino(v.getDestino(), v.getMetodo()))
                            .tiempoExpiracion((int) (tiempoRestanteMs / 1000))
                            .intentosRestantes(maxIntentosFallidos - v.getIntentosFallidos())
                            .build();
                })
                .orElse(null);
    }

    @Override
    public String enmascaraDestino(String destino, MetodoDosFactores metodo) {
        if (MetodoDosFactores.EMAIL.equals(metodo)) {
            String[] partes = destino.split("@");
            if (partes.length == 2) {
                String usuario = partes[0];
                String dominio = partes[1];
                String usuarioMascarado = usuario.substring(0, Math.min(2, usuario.length())) + "****";
                return usuarioMascarado + "@" + dominio;
            }
        } else if (MetodoDosFactores.SMS.equals(metodo)) {
            if (destino.length() >= 4) {
                return "*****" + destino.substring(destino.length() - 4);
            }
        }
        return destino;
    }

    private String generarCodigoAleatorio() {
        SecureRandom random = new SecureRandom();
        int codigo = random.nextInt(1000000);
        return String.format("%0" + CODIGO_LONGITUD + "d", codigo);
    }
}
