package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Proporciona métodos para acceder y manipular datos de usuarios en la base de datos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email correo electrónico del usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el correo electrónico especificado.
     * @param email correo electrónico a verificar
     * @return true si ya existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);

    /**
     * Obtiene todos los usuarios que tienen un rol específico.
     * @param rol rol del usuario (ADMIN, CLIENTE, TRIPULANTE, etc.)
     * @return lista de usuarios con ese rol
     */
    List<Usuario> findByRol(RolUsuario rol);

    /**
     * Obtiene todos los usuarios según su estado de actividad.
     * @param activo true para usuarios activos, false para inactivos
     * @return lista de usuarios con ese estado
     */
    List<Usuario> findByActivo(Boolean activo);

    /**
     * Busca un usuario por su identificador de cuenta de Google.
     * Utilizado en el flujo de autenticación OAuth2 con Google.
     * @param googleId identificador único de la cuenta de Google (sub del token)
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByGoogleId(String googleId);
}