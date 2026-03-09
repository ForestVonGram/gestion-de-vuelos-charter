package com.paeldav.backend.application.service.base;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;


public interface EmailService {

    @Async
    public void enviarEmailRecuperacion(String destinatario, String token, String nombreUsuario);

    @Async
    public void enviarEmailConfirmacionCambio(String destinatario, String nombreUsuario);

    @Async
    void enviarEmailRecuperacionConCodigo(String to, String codigo, String nombreCompleto);

    String buildMensajeRecuperacion(String nombre, String resetUrl);

    String buildMensajeConfirmacion(String nombre);

    void enviarCodigoVerificacion2FA(String destinatario, String codigo, String nombreUsuario);

    String buildMensajeCodigoVerificacion(String nombre, String codigo);
}

