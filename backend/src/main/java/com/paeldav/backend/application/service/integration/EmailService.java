package com.paeldav.backend.application.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Async
    public void enviarEmailRecuperacion(String destinatario, String token, String nombreUsuario) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Recuperación de contraseña - Charter Management");
            message.setText(buildMensajeRecuperacion(nombreUsuario, resetUrl));

            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación a {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailConfirmacionCambio(String destinatario, String nombreUsuario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Contraseña actualizada - Astra Nimbus Aviation");
            message.setText(buildMensajeConfirmacion(nombreUsuario));

            mailSender.send(message);
            log.info("Email de confirmación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación a {}: {}", destinatario, e.getMessage());
        }
    }

    private String buildMensajeRecuperacion(String nombre, String resetUrl) {
        return String.format("""
            Hola %s,
            
            Recibimos una solicitud para restablecer tu contraseña.
            
            Haz clic en el siguiente enlace para crear una nueva contraseña:
            %s
            
            Este enlace expirará en 1 hora.
            
            Si no solicitaste este cambio, puedes ignorar este mensaje.
            
            Saludos,
            El equipo de Astra Nimbus Aviation
            """, nombre, resetUrl);
    }

    private String buildMensajeConfirmacion(String nombre) {
        return String.format("""
            Hola %s,
            
            Tu contraseña ha sido actualizada exitosamente.
            
            Si no realizaste este cambio, contacta inmediatamente con soporte.
            
            Saludos,
            El equipo de Astra Nimbus Aviation
            """, nombre);
    }
}
