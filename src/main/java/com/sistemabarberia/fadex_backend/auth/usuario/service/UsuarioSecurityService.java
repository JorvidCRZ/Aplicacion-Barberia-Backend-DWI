package com.sistemabarberia.fadex_backend.auth.usuario.service;

import com.sistemabarberia.fadex_backend.auth.rol.Entity.Rol;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioSecurityService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioLogueado(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return usuarioRepository.findByUserWithRolesYPermisos(username).orElseThrow(
                ()->  new ResourceNotFoundException("Usuario no encontrado")
        );
    }

    public boolean isBarbero(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(
                a->a.getAuthority().equals("ROLE_barbero")
        );
    }

    public boolean isAdmin(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(
                a->a.getAuthority().equals("ROLE_admin")
        );
    }
    public boolean isCliente(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(
                a->a.getAuthority().equals("ROLE_cliente")
        );
    }
    public String getRolePrincipal(){


        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException(
                                "Usuario sin roles",
                                HttpStatus.FORBIDDEN
                        )
                );
    }

}
