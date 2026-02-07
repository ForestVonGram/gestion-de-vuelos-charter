package com.paeldav.backend.application.dto.auth;

import com.paeldav.backend.domain.enums.RolUsuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String nombreCompleto;
    private RolUsuario rol;
}
