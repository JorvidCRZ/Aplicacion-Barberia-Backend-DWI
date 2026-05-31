package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Integer > {
    Optional<Persona> findByUsuario(Usuario usuario);

    @Query("SELECT p FROM Persona p WHERE p.usuario.idUsuario = :usuarioId")
    Optional<Persona> findByUsuarioId(@Param("usuarioId") Integer usuarioId);
}
