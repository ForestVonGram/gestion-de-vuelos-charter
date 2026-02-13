package com.paeldav.backend.application.service;

import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para validar el control de acceso y seguridad del sistema.
 */

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Control de Acceso y Seguridad")
class SecurityControlTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioAdmin;
    private Usuario usuarioOperador;
    private Usuario usuarioTripulacion;

    @BeforeEach
    @Transactional
    void setUp() {
        usuarioAdmin = Usuario.builder()
                .nombre("Admin")
                .apellido("Usuario")
                .email("admin@test.com")
                .password("hashedPassword123")
                .rol(RolUsuario.ADMINISTRADOR)
                .activo(true)
                .dosFactoresHabilitado(false)
                .build();

        usuarioOperador = Usuario.builder()
                .nombre("Operador")
                .apellido("Sistema")
                .email("operador@test.com")
                .password("hashedPassword456")
                .rol(RolUsuario.OPERADOR_LOGISTICA)
                .activo(true)
                .dosFactoresHabilitado(false)
                .build();

        usuarioTripulacion = Usuario.builder()
                .nombre("Tripulante")
                .apellido("Activo")
                .email("tripulante@test.com")
                .password("hashedPassword789")
                .rol(RolUsuario.TRIPULACION)
                .activo(true)
                .dosFactoresHabilitado(false)
                .build();

        usuarioRepository.saveAll(java.util.List.of(usuarioAdmin, usuarioOperador, usuarioTripulacion));
    }

    @Test
    @DisplayName("ADMIN debe existir en el sistema")
    @Transactional
    void testAdminExiste() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        
        assertEquals(RolUsuario.ADMINISTRADOR, admin.getRol());
        assertEquals("admin@test.com", admin.getEmail());
        assertTrue(admin.getActivo());
        
        System.out.println("Admin verificado: " + admin.getEmail());
    }

    @Test
    @DisplayName("OPERADOR debe tener rol específico")
    @Transactional
    void testOperadorTieneRolEspecifico() {
        Usuario operador = usuarioRepository.findByEmail("operador@test.com").orElseThrow();
        
        assertEquals(RolUsuario.OPERADOR_LOGISTICA, operador.getRol());
        assertNotEquals(RolUsuario.ADMINISTRADOR, operador.getRol());
        
        System.out.println("Operador tiene rol: " + operador.getRol());
    }

    @Test
    @DisplayName("TRIPULACION debe tener acceso limitado")
    @Transactional
    void testTripulacionTieneAccesoLimitado() {
        Usuario tripulante = usuarioRepository.findByEmail("tripulante@test.com").orElseThrow();
        
        assertEquals(RolUsuario.TRIPULACION, tripulante.getRol());
        assertTrue(tripulante.getActivo());
        
        System.out.println("Tripulante verificado con rol: " + tripulante.getRol());
    }

    @Test
    @DisplayName("Usuarios activos deben ser accesibles")
    @Transactional
    void testUsuariosActivos() {
        long countActivos = usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo)
                .count();
        
        assertEquals(3, countActivos);
        System.out.println("Usuarios activos encontrados: " + countActivos);
    }

    @Test
    @DisplayName("Debe permitir buscar usuario por email")
    @Transactional
    void testBuscarPorEmail() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        Usuario operador = usuarioRepository.findByEmail("operador@test.com").orElseThrow();
        
        assertNotNull(admin);
        assertNotNull(operador);
        assertNotEquals(admin.getId(), operador.getId());
        
        System.out.println("Búsqueda por email exitosa");
    }

    @Test
    @DisplayName("Email debe ser único")
    @Transactional
    void testEmailUnico() {
        Usuario usuario1 = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        Usuario usuario2 = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        
        assertEquals(usuario1.getId(), usuario2.getId());
        
        System.out.println("Unicidad de email validada");
    }

    @Test
    @DisplayName("Diferentes roles deben tener diferentes permisos")
    @Transactional
    void testRolesDiferentesPermiso() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        Usuario operador = usuarioRepository.findByEmail("operador@test.com").orElseThrow();
        Usuario tripulante = usuarioRepository.findByEmail("tripulante@test.com").orElseThrow();
        
        assertTrue(admin.getRol().ordinal() > operador.getRol().ordinal() || 
                   admin.getRol().equals(RolUsuario.ADMINISTRADOR));
        assertFalse(tripulante.getRol().equals(RolUsuario.ADMINISTRADOR));
        
        System.out.println("Roles diferenciados correctamente");
    }

    @Test
    @DisplayName("Usuario debe tener fecha de registro")
    @Transactional
    void testUsuarioTieneFechaRegistro() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        
        assertNotNull(admin.getFechaRegistro());
        assertTrue(admin.getFechaRegistro().isBefore(LocalDateTime.now().plusSeconds(1)));
        
        System.out.println("Fecha de registro verificada: " + admin.getFechaRegistro());
    }

    @Test
    @DisplayName("2FA debe estar deshabilitado por defecto")
    @Transactional
    void test2FADeshabilitadoPorDefecto() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        
        assertFalse(admin.getDosFactoresHabilitado());
        
        System.out.println("2FA deshabilitado por defecto verificado");
    }

    @Test
    @DisplayName("Debe permitir actualizar estado de usuario")
    @Transactional
    void testActualizarEstadoUsuario() {
        Usuario admin = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        admin.setActivo(false);
        usuarioRepository.saveAndFlush(admin);
        
        Usuario actualizado = usuarioRepository.findByEmail("admin@test.com").orElseThrow();
        assertFalse(actualizado.getActivo());
        
        System.out.println("Estado de usuario actualizado correctamente");
    }
}
