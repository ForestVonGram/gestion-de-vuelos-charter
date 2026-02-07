package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.auth.SesionActivaDTO;
import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;

import java.util.List;

public interface SesionService {

    SesionActiva crearSesion(Usuario usuario, String token, String dispositivo, String direccionIp, String userAgent);

    List<SesionActivaDTO> obtenerSesionesActivas(Long usuarioId, String tokenActual);

    void revocarSesion(Long sesionId, Long usuarioId);

    void revocarTodasLasSesiones(Long usuarioId);

    void revocarOtrasSesiones(Long usuarioId, String tokenActual);

    boolean validarSesion(String token);

    void actualizarUltimaActividad(String token);

    String hashToken(String token);
}
