package com.sistemabarberia.fadex_backend.modules.reclamo.service.impl;

import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoEmailDTO;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReclamoEmailService implements IReclamoEmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String remitente;

    @Override
    public void enviarConfirmacionCliente(String email, ReclamoEmailDTO reclamo) {
        enviarCorreo(email, "FadeX | Reclamo registrado - " + reclamo.numeroReclamo(), generarHtmlConfirmacion(reclamo));
    }




    @Override
    public void enviarCambioEstado(String email, ReclamoEmailDTO reclamo) {
        enviarCorreo(email, "FadeX | Actualización de reclamo - " + reclamo.numeroReclamo(), generarHtmlCambioEstado(reclamo));
    }

    private void enviarCorreo(String destinatario, String asunto, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(message);
            //verificar que el correo llegue en formato HTML y no como texto plano
            log.info("Correo enviado correctamente a {} - {}", destinatario, asunto);
        } catch (Exception ex) {
            log.error("Error enviando correo a {} - {}", destinatario, asunto, ex);
        }
    }

    private String generarHtmlConfirmacion(ReclamoEmailDTO reclamo) {
        String fecha = reclamo.fechaReclamo() != null
                ? reclamo.fechaReclamo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";

        return plantillaBase(
                "Reclamo Registrado",
                """
                <p style="font-size:16px;">
                    Hola <strong>%s</strong>,
                </p>
    
                <p style="font-size:15px;color:#444;line-height:1.8;">
                    Hemos recibido tu reclamo correctamente.
                </p>
    
                <div style="
                    background:#faf7eb;
                    border-left:5px solid #D4AF37;
                    padding:18px;
                    margin:25px 0;
                    border-radius:6px;
                ">
                    <p style="margin:0; font-size:18px; font-weight:bold; color:#111111;">
                        %s
                    </p>
    
                    <p style="margin-top:14px;">
                        <strong>Tipo de reclamación:</strong> %s
                    </p>
    
                    <p style="margin-top:6px;">
                        <strong>Tipo de problema:</strong> %s
                    </p>
    
                    <p style="margin-top:6px;">
                        <strong>Fecha de registro:</strong> %s
                    </p>
    
                    <p style="margin-top:6px;">
                        <strong>Estado actual:</strong> %s
                    </p>
                </div>
    
                <p style="color:#555;line-height:1.8;">
                    Nuestro equipo revisará tu caso y se pondrá en contacto contigo
                    a la brevedad posible.
                </p>
    
                <p style="color:#555;line-height:1.8;">
                    Gracias por confiar en FadeX.
                </p>
                """
                        .formatted(reclamo.nombreCliente(), reclamo.numeroReclamo(), valorOrDefault(reclamo.tipoReclamacion()),
                                valorOrDefault(reclamo.tipoProblema()), fecha, reclamo.estado().replace("_", " "))
        );
    }

    private String generarHtmlCambioEstado(ReclamoEmailDTO reclamo) {
        String fecha = reclamo.fechaReclamo() != null
                ? reclamo.fechaReclamo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";

        return plantillaBase(
                "Actualización de Reclamo",
                """
                <p style="font-size:16px;">
                    Hola <strong>%s</strong>,
                </p>
    
                <p style="font-size:15px;color:#444;line-height:1.8;">
                    El estado de tu reclamo ha sido actualizado.
                </p>
    
                <div style="
                    background:#faf7eb;
                    border-left:5px solid #D4AF37;
                    padding:18px;
                    margin:25px 0;
                    border-radius:6px;
                ">
                    <p style="margin:0;">
                        <strong>Número:</strong> %s
                    </p>
    
                    <p style="margin-top:10px;">
                        <strong>Tipo de reclamación:</strong> %s
                    </p>
    
                    <p style="margin-top:6px;">
                        <strong>Tipo de problema:</strong> %s
                    </p>
    
                    <p style="margin-top:6px;">
                        <strong>Fecha de registro:</strong> %s
                    </p>
    
                    <p style="margin-top:10px;">
                        <strong>Estado actual:</strong> %s
                    </p>
                </div>
    
                <p style="color:#555;line-height:1.8;">
                    Puedes comunicarte con nosotros si necesitas más información.
                </p>
                """
                        .formatted(reclamo.nombreCliente(), reclamo.numeroReclamo(), valorOrDefault(reclamo.tipoReclamacion()),
                                valorOrDefault(reclamo.tipoProblema()), fecha, reclamo.estado().replace("_", " "))
        );
    }

    private String plantillaBase(String titulo, String contenido) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f5f5f5;
                font-family:Arial,sans-serif;
            ">

            <table width="100%%" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center">

                        <table width="600"
                               cellpadding="0"
                               cellspacing="0"
                               style="
                                   background:#ffffff;
                                   margin:30px auto;
                                   border-radius:12px;
                                   overflow:hidden;
                                   box-shadow:0 4px 15px rgba(0,0,0,.15);
                               ">

                            <tr>
                                <td style="
                                    background:#111111;
                                    padding:30px;
                                    text-align:center;
                                ">

                                    <h1 style="
                                        color:#D4AF37;
                                        margin:0;
                                        letter-spacing:4px;
                                        font-size:34px;
                                    ">
                                        FadeX
                                    </h1>

                                    <p style="
                                        margin-top:8px;
                                        color:#ffffff;
                                        font-size:12px;
                                        letter-spacing:3px;
                                        text-transform:uppercase;
                                    ">
                                        Sistema de Gestión de Barbería
                                    </p>

                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    background:#D4AF37;
                                    color:#111111;
                                    text-align:center;
                                    padding:16px;
                                    font-size:22px;
                                    font-weight:bold;
                                ">
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:35px;">
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td style="
                                    background:#111111;
                                    text-align:center;
                                    padding:20px;
                                ">
                                    <p style="
                                        color:#cccccc;
                                        margin:0;
                                        font-size:12px;
                                    ">
                                        © FadeX - Sistema de Gestión de Barbería.
                                        Todos los derechos reservados.
                                    </p>
                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>
            </table>

            </body>
            </html>
            """
                .formatted(titulo, contenido);
    }

private String valorOrDefault(String valor) {
    return valor != null ? valor.replace("_", " ") : "No especificado";
}

}