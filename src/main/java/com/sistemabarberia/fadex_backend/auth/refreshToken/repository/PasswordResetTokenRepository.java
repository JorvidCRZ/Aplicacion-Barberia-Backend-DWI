package com.sistemabarberia.fadex_backend.auth.refreshToken.repository;

import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);
}
