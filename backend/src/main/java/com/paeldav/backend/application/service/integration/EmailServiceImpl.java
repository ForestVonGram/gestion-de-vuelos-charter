package com.paeldav.backend.application.service.integration;

import com.paeldav.backend.application.service.base.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de enviar correos electrónicos del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Envía un correo para recuperación de contraseña con enlace.
     */
    @Override
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

    /**
     * Envía un correo confirmando el cambio de contraseña.
     */
    @Override
    @Async
    public void enviarEmailConfirmacionCambio(String destinatario, String nombreUsuario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Contraseña actualizada - AstraNimbus Aviation");
            message.setText(buildMensajeConfirmacion(nombreUsuario));

            mailSender.send(message);
            log.info("Email de confirmación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación a {}: {}", destinatario, e.getMessage());
        }
    }

    /**
     * Construye el mensaje de recuperación de contraseña.
     */
    @Override
    public String buildMensajeRecuperacion(String nombre, String resetUrl) {
        return String.format("""
            Hola %s,
            
            Recibimos una solicitud para restablecer tu contraseña.
            
            Haz clic en el siguiente enlace para crear una nueva contraseña:
            %s
            
            Este enlace expirará en 5 minutos.
            
            Si no solicitaste este cambio, puedes ignorar este mensaje.
            
            Saludos,
            El equipo de Astra Nimbus Aviation
            """, nombre, resetUrl);
    }

    /**
     * Construye el mensaje de confirmación de cambio de contraseña.
     */
    @Override
    public String buildMensajeConfirmacion(String nombre) {
        return String.format("""
            Hola %s,
            
            Tu contraseña ha sido actualizada exitosamente.
            
            Si no realizaste este cambio, contacta inmediatamente con soporte.
            
            Saludos,
            El equipo de Astra Nimbus Aviation
            """, nombre);
    }

    /**
     * Envía el código de verificación para autenticación en dos factores (2FA).
     */
    @Override
    @Async
    public void enviarCodigoVerificacion2FA(String destinatario, String codigo, String nombreUsuario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Tu código de autenticación de dos factores");
            message.setText(buildMensajeCodigoVerificacion(nombreUsuario, codigo));

            mailSender.send(message);
            log.info("Código de verificación 2FA enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar código 2FA a {}: {}", destinatario, e.getMessage());
        }
    }

    /**
     * Construye el mensaje con el código de verificación 2FA.
     */
    @Override
    public String buildMensajeCodigoVerificacion(String nombre, String codigo) {
        return String.format("""
            Hola %s,
            
            Tu código de autenticación de dos factores es:
            
            %s
            
            Este código expirará en 5 minutos.
            
            IMPORTANTE: Nunca compartas este código con nadie. 
            El equipo de Astra Nimbus Aviation nunca te pedirá este código por email.
            
            Saludos,
            El equipo de Astra Nimbus Aviation
            """, nombre, codigo);
    }

    /**
     * Envía un correo de recuperación usando un código en lugar de un enlace.
     */
    @Override
    @Async
    public void enviarEmailRecuperacionConCodigo(String to, String codigo, String nombreCompleto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Código de recuperación - AstraNimbus");

            String contenido = String.format(
                    "Hola %s,\n\n" +
                            "Has solicitado restablecer tu contraseña en AstraNimbus.\n\n" +
                            "Tu código de verificación es: %s\n\n" +
                            "Este código expirará en 5 minutos.\n\n" +
                            "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                            "Saludos,\n" +
                            "El equipo de AstraNimbus",
                    nombreCompleto, codigo
            );

            message.setText(contenido);
            mailSender.send(message);

            log.info("Email de recuperación con código enviado a: {}", to);

        } catch (Exception e) {
            log.error("Error al enviar email de recuperación con código a: {}", to, e);
            // No lanzar excepción aquí para no bloquear el flujo de recuperación
        }
    }
}