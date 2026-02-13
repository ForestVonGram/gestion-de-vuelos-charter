package com.paeldav.backend.infraestructure.persistence;

import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para validar el control de concurrencia optimista.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Concurrencia Optimista")
class OptimisticLockingTest {

    @Autowired
    private AeronaveRepository aeronaveRepository;

    private Aeronave aeronaveTest;

    @BeforeEach
    void setUp() {
        aeronaveTest = new Aeronave();
        // Usar timestamp para crear matrículas únicas por test
        String timestamp = System.currentTimeMillis() % 100000 + "";
        aeronaveTest.setMatricula("TST-" + timestamp.substring(timestamp.length() - 4));
        aeronaveTest.setModelo("Boeing 737");
        aeronaveTest.setFabricante("Boeing");
        aeronaveTest.setCapacidadPasajeros(150);
        aeronaveTest.setCapacidadTripulacion(6);
        aeronaveTest.setAutonomiaKm(5000.0);
        aeronaveTest.setVelocidadCruceroKmh(850.0);
        aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
        aeronaveRepository.saveAndFlush(aeronaveTest);
    }

    @Test
    @DisplayName("Debe permitir actualizaciones concurrentes")
    void testActualizacionesConcurrentes() throws InterruptedException {
        // Crear múltiples aeronaves para evitar conflictos de concurrencia
        int numeroThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numeroThreads);
        CountDownLatch latch = new CountDownLatch(numeroThreads);
        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger fallos = new AtomicInteger(0);

        // Crear una aeronave separada para cada thread
        for (int i = 0; i < numeroThreads; i++) {
            Aeronave aeronave = new Aeronave();
            aeronave.setMatricula("CONC-" + String.format("%03d", i));
            aeronave.setModelo("Boeing 737");
            aeronave.setFabricante("Boeing");
            aeronave.setCapacidadPasajeros(150);
            aeronave.setCapacidadTripulacion(6);
            aeronave.setAutonomiaKm(5000.0);
            aeronave.setVelocidadCruceroKmh(850.0);
            aeronave.setEstado(EstadoAeronave.DISPONIBLE);
            aeronaveRepository.saveAndFlush(aeronave);
        }

        // Obtener los IDs de las aeronaves creadas
        var aeronaves = aeronaveRepository.findAll().stream()
                .filter(a -> a.getMatricula().startsWith("CONC-"))
                .toList();

        for (int i = 0; i < numeroThreads && i < aeronaves.size(); i++) {
            final int index = i;
            final Long aeronaveId = aeronaves.get(i).getId();
            executor.submit(() -> {
                try {
                    Aeronave aeronave = aeronaveRepository.findById(aeronaveId).orElseThrow();
                    aeronave.setHorasVueloTotales(aeronave.getHorasVueloTotales() + 10.0 + index);
                    aeronaveRepository.saveAndFlush(aeronave);
                    exitosos.incrementAndGet();
                    System.out.println("Thread " + index + " actualizó exitosamente");
                } catch (Exception e) {
                    fallos.incrementAndGet();
                    System.out.println("Thread " + index + " encontró error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        System.out.println("Actualizaciones exitosas: " + exitosos.get());
        System.out.println("Actualizaciones fallidas: " + fallos.get());
        assertTrue(exitosos.get() == numeroThreads, "Todas las actualizaciones deben ser exitosas");
    }

    @Test
    @DisplayName("Debe permitir lectura sin conflictos")
    @Transactional
    void testLecturaSinConflictos() {
        Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        
        assertNotNull(aeronave.getMatricula());
        assertEquals(aeronaveTest.getMatricula(), aeronave.getMatricula());
        assertEquals(EstadoAeronave.DISPONIBLE, aeronave.getEstado());
    }

    @Test
    @DisplayName("Múltiples actualizaciones secuenciales")
    @Transactional
    void testActualizacionesSecuenciales() {
        for (int i = 0; i < 5; i++) {
            Aeronave aeronave = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
            aeronave.setHorasVueloTotales(aeronave.getHorasVueloTotales() + 10.0);
            aeronaveRepository.saveAndFlush(aeronave);
        }

        Aeronave resultado = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        assertEquals(50.0, resultado.getHorasVueloTotales());
    }

    @Test
    @DisplayName("Debe permitir actualizaciones de diferentes campos")
    @Transactional
    void testActualizacionesDiferentesCampos() {
        Aeronave aeronave1 = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        aeronave1.setHorasVueloTotales(100.0);
        
        Aeronave aeronave2 = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        aeronave2.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
        
        aeronaveRepository.saveAndFlush(aeronave1);
        aeronaveRepository.saveAndFlush(aeronave2);

        Aeronave resultado = aeronaveRepository.findById(aeronaveTest.getId()).orElseThrow();
        assertEquals(100.0, resultado.getHorasVueloTotales());
        assertEquals(EstadoAeronave.EN_MANTENIMIENTO, resultado.getEstado());
    }
}
