package com.sistemabarberia.fadex_backend.auth.refreshToken.repository;

import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);




    void deleteByUsuario_IdUsuario(Long id);
}
