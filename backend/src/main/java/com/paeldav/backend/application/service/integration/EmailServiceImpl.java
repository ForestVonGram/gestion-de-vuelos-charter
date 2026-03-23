package com.paeldav.backend.application.service.integration;

import com.paeldav.backend.application.service.base.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Servicio de correos electrónicos con plantillas HTML renderizadas por Thymeleaf.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    // ─────────────────────────────────────────────────────────────
    // Métodos públicos
    // ─────────────────────────────────────────────────────────────

    @Override
    @Async
    public void enviarEmailRecuperacion(String destinatario, String token, String nombreUsuario) {
        try {
            Context ctx = new Context(Locale.forLanguageTag("es"));
            ctx.setVariable("nombre", nombreUsuario);
            ctx.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + token);

            String html = templateEngine.process("emails/recuperacion", ctx);
            enviarHtml(destinatario, "Recuperación de contraseña - AstraNimbus Aviation", html);
            log.info("Email de recuperación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación a {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    @Async
    public void enviarEmailConfirmacionCambio(String destinatario, String nombreUsuario) {
        try {
            Context ctx = new Context(Locale.forLanguageTag("es"));
            ctx.setVariable("nombre", nombreUsuario);
            ctx.setVariable("fechaCambio", LocalDateTime.now());

            String html = templateEngine.process("emails/changeConfirmed", ctx);
            enviarHtml(destinatario, "Contraseña actualizada - AstraNimbus Aviation", html);
            log.info("Email de confirmación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación a {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    @Async
    public void enviarCodigoVerificacion2FA(String destinatario, String codigo, String nombreUsuario) {
        try {
            Context ctx = new Context(Locale.forLanguageTag("es"));
            ctx.setVariable("nombre", nombreUsuario);
            ctx.setVariable("codigo", codigo);

            String html = templateEngine.process("emails/2FA", ctx);
            enviarHtml(destinatario, "Tu código de autenticación - AstraNimbus Aviation", html);
            log.info("Código 2FA enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar código 2FA a {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    @Async
    public void enviarEmailRecuperacionConCodigo(String to, String codigo, String nombreCompleto) {
        try {
            Context ctx = new Context(Locale.forLanguageTag("es"));
            ctx.setVariable("nombre", nombreCompleto);
            ctx.setVariable("codigo", codigo);

            String html = templateEngine.process("emails/ResetCodePassword", ctx);
            enviarHtml(to, "Código de recuperación - AstraNimbus Aviation", html);
            log.info("Email de recuperación con código enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación con código a {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailLogin(String to, String nombreCompleto, String ip, String dispositivo) {
        try {
            Context ctx = new Context(Locale.forLanguageTag("es"));
            ctx.setVariable("nombre", nombreCompleto);
            ctx.setVariable("fechaHora", LocalDateTime.now());
            ctx.setVariable("ip", ip);
            ctx.setVariable("dispositivo", dispositivo);

            String html = templateEngine.process("emails/Login", ctx);
            enviarHtml(to, "Nuevo inicio de sesión - AstraNimbus Aviation", html);
            log.info("Email de inicio de sesión enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email de inicio de sesión a {}: {}", to, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Métodos de la interfaz que ya no construyen HTML directamente
    // (Se mantienen por compatibilidad con EmailService, pero delegan a Thymeleaf)
    // ─────────────────────────────────────────────────────────────

    @Override
    public String buildMensajeRecuperacion(String nombre, String resetUrl) {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("nombre", nombre);
        ctx.setVariable("resetUrl", resetUrl);
        return templateEngine.process("emails/recuperacion", ctx);
    }

    @Override
    public String buildMensajeConfirmacion(String nombre) {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("nombre", nombre);
        ctx.setVariable("fechaCambio", LocalDateTime.now());
        return templateEngine.process("emails/changeConfirmed", ctx);
    }

    @Override
    public String buildMensajeCodigoVerificacion(String nombre, String codigo) {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("nombre", nombre);
        ctx.setVariable("codigo", codigo);
        return templateEngine.process("emails/2FA", ctx);
    }

    // ─────────────────────────────────────────────────────────────
    // Helper de envío
    // ─────────────────────────────────────────────────────────────

    /**
     * Envía un correo con cuerpo HTML usando MimeMessage.
     */
    private void enviarHtml(String destinatario, String asunto, String html) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(html, true); // true = isHtml
        mailSender.send(mimeMessage);
    }
}