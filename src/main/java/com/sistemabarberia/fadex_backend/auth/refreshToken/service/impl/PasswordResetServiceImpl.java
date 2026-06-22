package com.sistemabarberia.fadex_backend.auth.refreshToken.service.impl;

import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.PasswordResetToken;
import com.sistemabarberia.fadex_backend.auth.refreshToken.repository.PasswordResetTokenRepository;
import com.sistemabarberia.fadex_backend.auth.refreshToken.service.PasswordResetService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final PersonaRepository personaRepository;

    @Override
    @Transactional
    public void forgotPassword(String correo) {

        System.out.println("Correo recibido: [" + correo + "]");

        personaRepository.findByEmail(correo.trim().toLowerCase())
                .ifPresentOrElse(persona -> {

                    System.out.println("Persona encontrada: " + persona.getPersonaId());

                    tokenRepository.deleteByEmail(correo);

                    String token = UUID.randomUUID().toString();

                    PasswordResetToken resetToken = PasswordResetToken.builder()
                            .token(token)
                            .email(correo)
                            .expiryDate(LocalDateTime.now().plusMinutes(15))
                            .build();

                    tokenRepository.save(resetToken);

                    System.out.println("Token guardado: " + token);

                    enviarCorreo(correo, token);

                    System.out.println("Correo enviado");

                }, () -> {

                    System.out.println("❌ NO SE ENCONTRÓ PERSONA CON ESE EMAIL");
                });
    }
    @Override
    @Transactional
    public void resetPassword(String token, String nuevaPassword) {

        // Buscar el token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verificar expiración
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado, solicita uno nuevo");
        }

        // Buscar usuario y cambiar contraseña
        Persona persona = personaRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Usuario usuario = persona.getUsuario();

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Eliminar token (ya fue usado, no puede reutilizarse)
        tokenRepository.delete(resetToken);
    }

    private void enviarCorreo(String correo, String token) {

        try {

            String enlace = "http://localhost:4200/reset-password?token=" + token;

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("🔐 Recuperación de contraseña - Fade X");

            String html = """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;background:#111111;font-family:Arial,sans-serif;">
            
                <div style="max-width:600px;margin:40px auto;background:#1a1a1a;border-radius:12px;overflow:hidden;border:1px solid #333;">
                    
                    <div style="background:#D4AF37;padding:25px;text-align:center;">
                        <h1 style="margin:0;color:#000;">💈 FADEX BARBER</h1>
                    </div>

                    <div style="padding:35px;color:#ffffff;">

                        <h2>Recuperación de contraseña</h2>

                        <p>
                            Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.
                        </p>

                        <p>
                            Haz clic en el siguiente botón para crear una nueva contraseña:
                        </p>

                        <div style="text-align:center;margin:35px 0;">
                            <a href="%s"
                               style="
                                    background:#D4AF37;
                                    color:#000;
                                    padding:14px 28px;
                                    text-decoration:none;
                                    border-radius:8px;
                                    font-weight:bold;
                                    display:inline-block;">
                                Restablecer contraseña
                            </a>
                        </div>

                        <p>
                            ⏳ Este enlace expirará en <strong>15 minutos</strong>.
                        </p>

                        <p style="color:#999;">
                            Si no solicitaste este cambio, puedes ignorar este correo.
                        </p>

                        <hr style="border:none;border-top:1px solid #333;margin:25px 0;">

                        <p style="font-size:12px;color:#777;text-align:center;">
                            © 2026 Fadex Barber - Sistema de Reservas
                        </p>

                    </div>

                </div>

            </body>
            </html>
            """.formatted(enlace);

            helper.setText(html, true);

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo", e);
        }
    }
    @Override
    @Transactional
    public void changePassword(String passwordActual, String passwordNueva, CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario(); // directo, sin buscar en BD

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

}
