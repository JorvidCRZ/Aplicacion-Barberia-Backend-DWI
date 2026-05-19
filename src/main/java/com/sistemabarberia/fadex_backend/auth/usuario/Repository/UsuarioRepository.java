package com.sistemabarberia.fadex_backend.auth.usuario.Repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

     @Query(
             """
                     select u FROM Usuario u
                     left join  fetch u.roles r
                     left join fetch r.permisos
                     where u.user =  :user
                                                                               \s
             
          """
     )
     Optional<Usuario> findByUserWithRolesYPermisos(String user);

    Optional<Usuario> findByUser(String user);
    @EntityGraph(attributePaths = "roles")
    List<Usuario> findAll();
    boolean existsByUser(String user);

}
