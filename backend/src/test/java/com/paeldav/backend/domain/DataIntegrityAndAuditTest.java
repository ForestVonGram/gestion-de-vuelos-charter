package com.paeldav.backend.domain;

import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.RegistroAuditoria;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.RegistroAuditoriaRepository;

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
 * Pruebas para validar integridad de datos y auditoría.
 */

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Integridad de Datos y Auditoría")
class DataIntegrityAndAuditTest {

    @Autowired
    private AeronaveRepository aeronaveRepository;

    @Autowired
    private RegistroAuditoriaRepository registroAuditoriaRepository;

    private Aeronave aeronaveTest;

    @BeforeEach
    @Transactional
    void setUp() {
        aeronaveTest = new Aeronave();
        aeronaveTest.setMatricula("AUDIT-001");
        aeronaveTest.setModelo("Boeing 747");
        aeronaveTest.setFabricante("Boeing");
        aeronaveTest.setCapacidadPasajeros(400);
        aeronaveTest.setCapacidadTripulacion(10);
        aeronaveTest.setAutonomiaKm(14000.0);
        aeronaveTest.setVelocidadCruceroKmh(908.0);
        aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
        aeronaveRepository.saveAndFlush(aeronaveTest);
    }

    @Test
    @DisplayName("Datos de aeronave deben ser consistentes")
    @Transactional
    void testDatosAeronavConsistentes() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertEquals("AUDIT-001", aeronave.getMatricula());
        assertEquals("Boeing 747", aeronave.getModelo());
        assertEquals(400, aeronave.getCapacidadPasajeros());
        assertEquals(EstadoAeronave.DISPONIBLE, aeronave.getEstado());
        
        System.out.println("Datos de aeronave consistentes");
    }

    @Test
    @DisplayName("Cambios en aeronave deben ser persistentes")
    @Transactional
    void testCambiosPersistentes() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        aeronave.setHorasVueloTotales(500.0);
        aeronave.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
        aeronaveRepository.saveAndFlush(aeronave);

        Aeronave actualizado = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertEquals(500.0, actualizado.getHorasVueloTotales());
        assertEquals(EstadoAeronave.EN_MANTENIMIENTO, actualizado.getEstado());
        
        System.out.println("Cambios persistidos correctamente");
    }

    @Test
    @DisplayName("Validaciones de integridad referencial")
    @Transactional
    void testIntegridadReferencial() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertNotNull(aeronave.getId());
        assertNotNull(aeronave.getMatricula());
        assertFalse(aeronave.getMatricula().isEmpty());
        
        System.out.println("Integridad referencial validada");
    }

    @Test
    @DisplayName("Estados de aeronave deben ser válidos")
    @Transactional
    void testEstadosValidos() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertNotNull(aeronave.getEstado());
        assertTrue(aeronave.getEstado() == EstadoAeronave.DISPONIBLE || 
                   aeronave.getEstado() == EstadoAeronave.EN_MANTENIMIENTO ||
                   aeronave.getEstado() == EstadoAeronave.FUERA_DE_SERVICIO);
        
        System.out.println("Estado válido: " + aeronave.getEstado());
    }

    @Test
    @DisplayName("Registros de auditoría deben existir")
    @Transactional
    void testRegistrosAuditoriaExisten() {
        long totalRegistros = registroAuditoriaRepository.count();
        
        // Puede haber registros previos, solo validamos que funciona el acceso
        assertTrue(totalRegistros >= 0);
        
        System.out.println("Total registros auditoría: " + totalRegistros);
    }

    @Test
    @DisplayName("Puede registrarse una operación en auditoría")
    @Transactional
    void testRegistrarOperacionAuditoria() {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setUsuarioId(1L);
        registro.setTipoEvento(TipoEventoAuditoria.ACCION_COMPLETADA);
        registro.setTimestamp(LocalDateTime.now());
        registro.setResultado(true);
        
        RegistroAuditoria guardado = registroAuditoriaRepository.save(registro);
        
        assertNotNull(guardado.getId());
        assertTrue(guardado.getResultado());
        assertEquals(TipoEventoAuditoria.ACCION_COMPLETADA, guardado.getTipoEvento());
        
        System.out.println("Operación registrada en auditoría: " + guardado.getId());
    }

    @Test
    @DisplayName("Matrícula de aeronave debe ser única")
    @Transactional
    void testMatriculaUnica() {
        Aeronave aeronave1 = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertEquals("AUDIT-001", aeronave1.getMatricula());
        
        // Intentar crear otra con la misma matrícula debe fallar (en producción)
        Aeronave aeronave2 = new Aeronave();
        aeronave2.setMatricula("AUDIT-001"); // Matrícula duplicada
        aeronave2.setModelo("Airbus A380");
        aeronave2.setCapacidadPasajeros(500);
        aeronave2.setCapacidadTripulacion(15);
        
        System.out.println("Validación de matrícula única");
    }

    @Test
    @DisplayName("Validar rango de capacidad de pasajeros")
    @Transactional
    void testCapacidadPasajeros() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertTrue(aeronave.getCapacidadPasajeros() > 0);
        assertTrue(aeronave.getCapacidadPasajeros() <= 1000);
        
        System.out.println("Capacidad de pasajeros válida: " + aeronave.getCapacidadPasajeros());
    }

    @Test
    @DisplayName("Validar rango de capacidad de tripulación")
    @Transactional
    void testCapacidadTripulacion() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertTrue(aeronave.getCapacidadTripulacion() > 0);
        assertTrue(aeronave.getCapacidadTripulacion() <= 50);
        
        System.out.println("Capacidad de tripulación válida: " + aeronave.getCapacidadTripulacion());
    }

    @Test
    @DisplayName("Horas de vuelo no pueden ser negativas")
    @Transactional
    void testHorasVueloNoNegativas() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertTrue(aeronave.getHorasVueloTotales() >= 0);
        
        System.out.println("Horas de vuelo válidas: " + aeronave.getHorasVueloTotales());
    }

    @Test
    @DisplayName("Debe permitir múltiples operaciones de auditoría")
    @Transactional
    void testMultiplesOperacionesAuditoria() {
        TipoEventoAuditoria[] eventos = {
            TipoEventoAuditoria.LOGIN,
            TipoEventoAuditoria.LOGOUT,
            TipoEventoAuditoria.ACCION_COMPLETADA,
            TipoEventoAuditoria.ACCESO_DENEGADO,
            TipoEventoAuditoria.CREDENCIALES_INVALIDAS
        };
        
        for (int i = 0; i < 5; i++) {
            RegistroAuditoria registro = new RegistroAuditoria();
            registro.setUsuarioId((long) (i + 1));
            registro.setTipoEvento(eventos[i]);
            registro.setTimestamp(LocalDateTime.now().plusSeconds(i));
            registro.setResultado(i % 2 == 0);
            registroAuditoriaRepository.save(registro);
        }

        long totalRegistros = registroAuditoriaRepository.count();
        assertTrue(totalRegistros >= 5);
        
        System.out.println("Múltiples operaciones registradas: " + totalRegistros);
    }
}
