package com.paeldav.backend.certificacion;

import com.paeldav.backend.application.service.integration.ValidadorCertificacionesService;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.CertificacionVencidaException;
import com.paeldav.backend.exception.RequisitoTecnicoNoMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidadorCertificacionesService Tests")
class ValidadorCertificacionesServiceTest {

    private ValidadorCertificacionesService validador;
    private Tripulante tripulantePiloto;
    private Tripulante tripulanteAuxiliar;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        validador = new ValidadorCertificacionesService();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .rol(RolUsuario.TRIPULACION)
                .build();

        tripulantePiloto = Tripulante.builder()
                .id(1L)
                .usuario(usuario)
                .numeroLicencia("PIL-001")
                .tipoLicencia("ATPL")
                .fechaExpedicionLicencia(LocalDate.now().minusYears(2))
                .fechaVencimientoLicencia(LocalDate.now().plusYears(1))
                .horasVueloTotales(1500.0)
                .horasVueloMes(80.0)
                .estado(EstadoTripulante.DISPONIBLE)
                .esPiloto(true)
                .certificaciones("ATPL, IFR, MCC")
                .build();

        tripulanteAuxiliar = Tripulante.builder()
                .id(2L)
                .usuario(usuario)
                .numeroLicencia("AUX-001")
                .tipoLicencia("FLIGHT ATTENDANT")
                .fechaExpedicionLicencia(LocalDate.now().minusYears(1))
                .fechaVencimientoLicencia(LocalDate.now().plusYears(2))
                .horasVueloTotales(500.0)
                .horasVueloMes(60.0)
                .estado(EstadoTripulante.DISPONIBLE)
                .esPiloto(false)
                .certificaciones("Safety, Emergency")
                .build();
    }

    @Nested
    @DisplayName("Validar Licencia Vigente Tests")
    class ValidarLicenciaVigenteTests {

        @Test
        @DisplayName("Licencia vigente no lanza excepcion")
        void validarLicenciaVigente_LicenciaValida_NoLanzaExcepcion() {
            assertDoesNotThrow(() -> validador.validarLicenciaVigente(tripulantePiloto));
        }

        @Test
        @DisplayName("Licencia vencida lanza excepcion")
        void validarLicenciaVigente_LicenciaVencida_LanzaExcepcion() {
            tripulantePiloto.setFechaVencimientoLicencia(LocalDate.now().minusDays(1));

            assertThrows(CertificacionVencidaException.class, () -> {
                validador.validarLicenciaVigente(tripulantePiloto);
            });
        }

        @Test
        @DisplayName("Licencia vencida hoy lanza excepcion")
        void validarLicenciaVigente_LicenciaVenceHoy_LanzaExcepcion() {
            tripulantePiloto.setFechaVencimientoLicencia(LocalDate.now());

            assertThrows(CertificacionVencidaException.class, () -> {
                validador.validarLicenciaVigente(tripulantePiloto);
            });
        }
    }

    @Nested
    @DisplayName("Validar Requisitos Piloto Tests")
    class ValidarRequisitosPilotoTests {

        @Test
        @DisplayName("Piloto con requisitos validos no lanza excepcion")
        void validarRequisitosPiloto_PilotoValido_NoLanzaExcepcion() {
            assertDoesNotThrow(() -> validador.validarRequisitosPiloto(tripulantePiloto));
        }

        @Test
        @DisplayName("Piloto sin horas minimas lanza excepcion")
        void validarRequisitosPiloto_SinHorasMinimas_LanzaExcepcion() {
            tripulantePiloto.setHorasVueloTotales(500.0);

            assertThrows(RequisitoTecnicoNoMetException.class, () -> {
                validador.validarRequisitosPiloto(tripulantePiloto);
            });
        }

        @Test
        @DisplayName("Piloto sin certificaciones lanza excepcion")
        void validarRequisitosPiloto_SinCertificaciones_LanzaExcepcion() {
            tripulantePiloto.setCertificaciones(null);

            assertThrows(RequisitoTecnicoNoMetException.class, () -> {
                validador.validarRequisitosPiloto(tripulantePiloto);
            });
        }

        @Test
        @DisplayName("No piloto no valida requisitos de piloto")
        void validarRequisitosPiloto_NoEsPiloto_NoLanzaExcepcion() {
            tripulanteAuxiliar.setEsPiloto(false);

            assertDoesNotThrow(() -> validador.validarRequisitosPiloto(tripulanteAuxiliar));
        }
    }

    @Nested
    @DisplayName("Validar Requisitos Auxiliar Tests")
    class ValidarRequisitosAuxiliarTests {

        @Test
        @DisplayName("Auxiliar con certificaciones no lanza excepcion")
        void validarRequisitosAuxiliar_ConCertificaciones_NoLanzaExcepcion() {
            assertDoesNotThrow(() -> validador.validarRequisitosAuxiliar(tripulanteAuxiliar));
        }

        @Test
        @DisplayName("Auxiliar sin certificaciones lanza excepcion")
        void validarRequisitosAuxiliar_SinCertificaciones_LanzaExcepcion() {
            tripulanteAuxiliar.setCertificaciones(null);

            assertThrows(RequisitoTecnicoNoMetException.class, () -> {
                validador.validarRequisitosAuxiliar(tripulanteAuxiliar);
            });
        }

        @Test
        @DisplayName("Piloto no valida requisitos de auxiliar")
        void validarRequisitosAuxiliar_EsPiloto_NoLanzaExcepcion() {
            assertDoesNotThrow(() -> validador.validarRequisitosAuxiliar(tripulantePiloto));
        }
    }

    @Nested
    @DisplayName("Es Valido Para Volar Tests")
    class EsValidoParaVolarTests {

        @Test
        @DisplayName("Tripulante valido retorna true")
        void esValidoParaVolar_TripulanteValido_RetornaTrue() {
            assertTrue(validador.esValidoParaVolar(tripulantePiloto));
        }

        @Test
        @DisplayName("Tripulante con licencia vencida retorna false")
        void esValidoParaVolar_LicenciaVencida_RetornaFalse() {
            tripulantePiloto.setFechaVencimientoLicencia(LocalDate.now().minusDays(1));

            assertFalse(validador.esValidoParaVolar(tripulantePiloto));
        }

        @Test
        @DisplayName("Piloto sin horas minimas retorna false")
        void esValidoParaVolar_SinHorasMinimas_RetornaFalse() {
            tripulantePiloto.setHorasVueloTotales(500.0);

            assertFalse(validador.esValidoParaVolar(tripulantePiloto));
        }
    }

    @Nested
    @DisplayName("Validar Horas Vuelo Mensuales Tests")
    class ValidarHorasVueloMensualesTests {

        @Test
        @DisplayName("Horas mensuales dentro del limite no lanza excepcion")
        void validarHorasVueloMensuales_DentroDelLimite_NoLanzaExcepcion() {
            tripulantePiloto.setHorasVueloMes(90.0);

            assertDoesNotThrow(() -> validador.validarHorasVueloMensuales(tripulantePiloto));
        }

        @Test
        @DisplayName("Horas mensuales en el limite no lanza excepcion")
        void validarHorasVueloMensuales_EnElLimite_NoLanzaExcepcion() {
            tripulantePiloto.setHorasVueloMes(100.0);

            assertDoesNotThrow(() -> validador.validarHorasVueloMensuales(tripulantePiloto));
        }

        @Test
        @DisplayName("Horas mensuales excedidas lanza excepcion")
        void validarHorasVueloMensuales_Excedidas_LanzaExcepcion() {
            tripulantePiloto.setHorasVueloMes(120.0);

            assertThrows(RequisitoTecnicoNoMetException.class, () -> {
                validador.validarHorasVueloMensuales(tripulantePiloto);
            });
        }
    }
}
