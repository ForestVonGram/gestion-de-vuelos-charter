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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        // Verificar que el email no exista
        if (usuarioRepository.existsByEmail(usuarioCreateDTO.getEmail())) {
            throw new UsuarioYaExisteException(
                    "Ya existe un usuario con el email: " + usuarioCreateDTO.getEmail()
            );
        }

        // Convertir DTO a entidad
        Usuario usuario = usuarioMapper.toEntity(usuarioCreateDTO);

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuarioCreateDTO.getPassword()));

        // Asegurar que el usuario está activo al crearse
        usuario.setActivo(true);

        // Guardar en base de datos
        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    @Transactional
    public UsuarioDTO editarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        // Validar email único si se está actualizando
        if (usuarioUpdateDTO.getEmail() != null &&
                !usuarioUpdateDTO.getEmail().equals(usuario.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioUpdateDTO.getEmail())) {
            throw new UsuarioYaExisteException(
                    "Ya existe un usuario con el email: " + usuarioUpdateDTO.getEmail()
            );
        }

        // Actualizar campos
        usuarioMapper.updateEntityFromDTO(usuarioUpdateDTO, usuario);

        // Si se proporciona nueva contraseña, encriptarla
        if (usuarioUpdateDTO.getPassword() != null && !usuarioUpdateDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioUpdateDTO.getPassword()));
        }

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + id
                ));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
}
