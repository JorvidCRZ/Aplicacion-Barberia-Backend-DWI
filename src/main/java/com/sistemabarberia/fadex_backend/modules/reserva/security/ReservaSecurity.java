package com.sistemabarberia.fadex_backend.modules.reserva.security;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaSecurity {

    private final ReservaRepository reservaRepository;
    private final UsuarioSecurityService securityService;

    public boolean isOwner(Long reservaId, Authentication auth) {

        Usuario usuario = (Usuario) auth.getPrincipal();

        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                ()->new BusinessException("la reserva no existe", HttpStatus.BAD_REQUEST)
        );

        if (reserva==null){
            return false;
        }
        return reserva.getCliente().getClienteId().equals(usuario.getIdUsuario());

    }

    public boolean canDeleteReserva(Long reservaId, Authentication auth){

        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                ()-> new ResourceNotFoundException("no existe reserva")
        );
        if (reserva==null){
            return false;
        }
        return isOwner(reserva.getId(),auth) || securityService.isAdmin() || securityService.isBarbero();
    }
}
