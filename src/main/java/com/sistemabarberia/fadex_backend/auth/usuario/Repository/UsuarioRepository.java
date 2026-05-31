package com.sistemabarberia.fadex_backend.auth.usuario.Repository;

import com.sistemabarberia.fadex_backend.auth.permiso.entity.Permiso;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioTablaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    boolean existsByCorreo(String correo);


    @Query("""
    SELECT u, p, r
    FROM Usuario u
    LEFT JOIN Persona p ON p.usuario.idUsuario = u.idUsuario
    LEFT JOIN u.roles r
""")
    Page<Object[]> listarUsuariosTabla(Pageable pageable);


    @Query("""
SELECT DISTINCT u
FROM Usuario u
LEFT JOIN FETCH u.roles r
WHERE

    (:rol IS NULL OR LOWER(r.nombre) = LOWER(:rol))

AND (

    :tieneQr IS NULL

    OR (
        :tieneQr = true
        AND u.qrToken IS NOT NULL
        AND u.qrToken <> ''
    )

    OR (
        :tieneQr = false
        AND (
            u.qrToken IS NULL
            OR u.qrToken = ''
        )
    )
)

AND (

    :multiplesRoles IS NULL

    OR (
        :multiplesRoles = true
        AND SIZE(u.roles) > 1
    )

    OR (
        :multiplesRoles = false
        AND SIZE(u.roles) = 1
    )
)
""")
    Page<Usuario> filtrarUsuarios(
            @Param("rol") String rol,
            @Param("tieneQr") Boolean tieneQr,
            @Param("multiplesRoles") Boolean multiplesRoles,
            Pageable pageable
    );


    @Query("""
SELECT DISTINCT u
FROM Usuario u
LEFT JOIN FETCH u.roles r
LEFT JOIN Persona p ON p.usuario.idUsuario = u.idUsuario
WHERE

    LOWER(u.user) LIKE LOWER(CONCAT('%', :texto, '%'))

    OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))

    OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
""")
    Page<Usuario> buscarUsuarios(
            @Param("texto") String texto,
            Pageable pageable
    );


    @Query("""
    SELECT DISTINCT p
    FROM Usuario u
    JOIN u.roles r
    JOIN r.permisos p
    WHERE u.idUsuario = :idUsuario
""")
    Page<Permiso> findPermisosByUsuarioId(
            @Param("idUsuario") Integer idUsuario,
            Pageable pageable
    );

}
