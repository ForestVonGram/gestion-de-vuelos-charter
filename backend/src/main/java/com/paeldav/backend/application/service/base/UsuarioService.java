package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioUpdateDTO;

import java.util.List;

/**
 * Interfaz para la gestión de usuarios.
 */
public interface UsuarioService {

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param usuarioCreateDTO DTO con los datos del nuevo usuario
     * @return DTO del usuario creado
     * @throws com.paeldav.backend.exception.UsuarioYaExisteException si el email ya existe
     */
    UsuarioDTO crearUsuario(UsuarioCreateDTO usuarioCreateDTO);

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return DTO del usuario
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    UsuarioDTO obtenerUsuarioPorId(Long id);

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return lista de DTOs de usuarios
     */
    List<UsuarioDTO> obtenerTodosUsuarios();

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id ID del usuario a actualizar
     * @param usuarioUpdateDTO DTO con los datos a actualizar
     * @return DTO del usuario actualizado
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    UsuarioDTO editarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO);

    /**
     * Desactiva un usuario (soft delete).
     *
     * @param id ID del usuario a desactivar
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    void desactivarUsuario(Long id);

    /**
     * Activa un usuario previamente desactivado.
     *
     * @param id ID del usuario a activar
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    void activarUsuario(Long id);

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param id ID del usuario
     * @param nuevaPassword nueva contraseña
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    void cambiarPassword(Long id, String nuevaPassword);
}
