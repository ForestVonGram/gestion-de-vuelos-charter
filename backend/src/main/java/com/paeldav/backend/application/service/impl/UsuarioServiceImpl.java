package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.paeldav.backend.application.mapper.UsuarioMapper;
import com.paeldav.backend.application.service.base.UsuarioService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.UsuarioYaExisteException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de usuarios.
 * Se encarga del ciclo de vida de las cuentas, cifrado de credenciales y validación de datos únicos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    // Dependencias inyectadas para persistencia, mapeo y seguridad (cifrado de contraseñas)
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        log.info("Intentando registrar usuario con email: {}", usuarioCreateDTO.getEmail());

        // Regla de negocio: El email debe ser único en todo el sistema
        if (usuarioRepository.existsByEmail(usuarioCreateDTO.getEmail())) {
            log.warn("Registro fallido: email {} ya existe", usuarioCreateDTO.getEmail());
            throw new UsuarioYaExisteException(
                    "Ya existe un usuario con el email: " + usuarioCreateDTO.getEmail()
            );
        }

        // Convertir el DTO de entrada en la entidad de dominio
        Usuario usuario = usuarioMapper.toEntity(usuarioCreateDTO);

        // Seguridad: Encriptar la contraseña en texto plano antes de persistirla
        usuario.setPassword(passwordEncoder.encode(usuarioCreateDTO.getPassword()));

        // Asegurar que el usuario tenga acceso inmediato al sistema tras su creación
        usuario.setActivo(true);

        // Guardar el registro en la base de datos y retornar el DTO resultante
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario registrado exitosamente - ID: {}, Email: {}, Rol: {}",
                usuario.getId(), usuario.getEmail(), usuario.getRol());

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        // Buscar al usuario por su clave primaria o lanzar una excepción si no existe
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosUsuarios() {
        // Extraer y retornar el listado completo de usuarios registrados
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    @Transactional
    public UsuarioDTO editarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {
        log.info("Editando usuario ID: {}", id);
        // Recuperar el usuario actual para aplicar las modificaciones
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        // Validar que si el usuario cambia su email, el nuevo correo no esté tomado por alguien más
        if (usuarioUpdateDTO.getEmail() != null &&
                !usuarioUpdateDTO.getEmail().equals(usuario.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioUpdateDTO.getEmail())) {
            log.warn("Edición fallida: email {} ya está en uso", usuarioUpdateDTO.getEmail());
            throw new UsuarioYaExisteException(
                    "Ya existe un usuario con el email: " + usuarioUpdateDTO.getEmail()
            );
        }

        // Aplicar la actualización de los campos permitidos desde el DTO hacia la entidad
        usuarioMapper.updateEntityFromDTO(usuarioUpdateDTO, usuario);

        // Si la petición incluye una nueva contraseña, encriptarla y sobreescribir la anterior
        if (usuarioUpdateDTO.getPassword() != null && !usuarioUpdateDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioUpdateDTO.getPassword()));
            log.debug("Contraseña actualizada para usuario ID: {}", id);
        }

        // Guardar los cambios estructurales en la base de datos
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario ID: {} actualizado correctamente", id);

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id) {
        log.info("Desactivando usuario ID: {}", id);
        // Buscar el usuario objetivo
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        // Revocar el acceso al sistema mediante el borrado lógico (estado inactivo)
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario ID: {} desactivado", id);
    }

    @Override
    @Transactional
    public void activarUsuario(Long id) {
        log.info("Activando usuario ID: {}", id);
        // Buscar el usuario objetivo
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        // Restaurar el acceso al sistema
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuario ID: {} activado", id);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) {
        log.info("Cambio de contraseña para usuario ID: {}", id);
        // Recuperar el usuario que solicitó el cambio de credenciales
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        // Cifrar la nueva contraseña ingresada y actualizar la entidad
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada para usuario ID: {}", id);
    }
}