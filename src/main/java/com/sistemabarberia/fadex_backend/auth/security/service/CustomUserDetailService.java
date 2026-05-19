package com.sistemabarberia.fadex_backend.auth.security.service;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUserWithRolesYPermisos(user).orElseThrow(()->new UsernameNotFoundException("usuario no encontrado"));


        Set<GrantedAuthority> authorities = new HashSet<>();
        usuario.getRoles().forEach(rol->{
            authorities.add(new SimpleGrantedAuthority("ROLE_" +  rol.getNombre()));
            rol.getPermisos().forEach(permiso->{
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            });
        });

        return new CustomUserDetails(usuario, authorities);
    }



}

