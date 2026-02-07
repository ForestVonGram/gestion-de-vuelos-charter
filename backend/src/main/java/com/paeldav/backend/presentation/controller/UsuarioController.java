package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.paeldav.backend.application.service.base.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Crea un nuevo usuario.
     *
     * @param usuarioCreateDTO DTO con los datos del usuario
     * @return ResponseEntity con el usuario creado
     */
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        UsuarioDTO usuarioCreado = usuarioService.crearUsuario(usuarioCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return ResponseEntity con el usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return ResponseEntity con la lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id ID del usuario a actualizar
     * @param usuarioUpdateDTO DTO con los datos a actualizar
     * @return ResponseEntity con el usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> editarUsuario(@PathVariable Long id,
                                                     @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.editarUsuario(id, usuarioUpdateDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Desactiva un usuario.
     *
     * @param id ID del usuario a desactivar
     * @return ResponseEntity sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activa un usuario previamente desactivado.
     *
     * @param id ID del usuario a activar
     * @return ResponseEntity sin contenido
     */
    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable Long id) {
        usuarioService.activarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param id ID del usuario
     * @param request objeto con la nueva contraseña
     * @return ResponseEntity sin contenido
     */
    @PostMapping("/{id}/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id,
                                               @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(id, request.getNuevaPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * Clase interna para solicitud de cambio de contraseña.
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CambiarPasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "La nueva contraseña es obligatoria")
        @jakarta.validation.constraints.Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        private String nuevaPassword;
    }
}
