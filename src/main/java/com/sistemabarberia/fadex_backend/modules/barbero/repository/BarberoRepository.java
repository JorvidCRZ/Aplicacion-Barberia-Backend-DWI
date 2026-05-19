package com.sistemabarberia.fadex_backend.modules.barbero.repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero,Integer>{


    boolean existsByPersona_PersonaId(Integer personaId);
    Optional<Barbero> findByPersona_Usuario_IdUsuario(Integer usuarioId);

    Optional<Barbero> findByPersonaUsuario(Usuario usuario);


}
