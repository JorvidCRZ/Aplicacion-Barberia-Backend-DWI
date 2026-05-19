package com.sistemabarberia.fadex_backend.auth.refreshToken.service;

import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.RefreshToken;
import com.sistemabarberia.fadex_backend.auth.refreshToken.repository.RefreshTokenRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken crearRefreshToken(Usuario usuario) {
        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        return refreshTokenRepository.save(token);

    }

    public RefreshToken validarRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->new BusinessException("refresh  token no existe",HttpStatus.NOT_FOUND));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("refresh token expiry", HttpStatus.BAD_REQUEST);
        }

        if (refreshToken.isRevoked()){
            throw new BusinessException("refresh token revoked", HttpStatus.BAD_REQUEST);
        }
        return refreshToken;
    }

    public void RevokedAllUserTokens(Long userId){
        refreshTokenRepository.deleteByUsuario_IdUsuario(userId);
    }
}