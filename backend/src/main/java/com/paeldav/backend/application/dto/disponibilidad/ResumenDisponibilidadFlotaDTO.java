package com.paeldav.backend.application.dto.disponibilidad;

import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO que representa un resumen del estado de disponibilidad de toda la flota.
 * Incluye contadores por estado y lista de aeronaves disponibles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenDisponibilidadFlotaDTO {

    /**
     * Total de aeronaves en la flota.
     */
    private Integer totalAeronaves;

    /**
     * Número de aeronaves disponibles para operación.
     */
    private Integer aeronavesDisponibles;

    /**
     * Número de aeronaves actualmente en vuelo.
     */
    private Integer aeronavesEnVuelo;

    /**
     * Número de aeronaves en mantenimiento.
     */
    private Integer aeronavesEnMantenimiento;

    /**
     * Número de aeronaves fuera de servicio (bloqueadas).
     */
    private Integer aeronavesFueraDeServicio;

    /**
     * Mapa con el conteo de aeronaves por cada estado.
     */
    private Map<String, Integer> contadorPorEstado;

    /**
     * Lista de aeronaves actualmente disponibles.
     */
    private List<AeronaveDTO> listaAeronavesDisponibles;

    /**
     * Lista de aeronaves fuera de servicio (bloqueadas).
     */
    private List<AeronaveDTO> listaAerronavesBloqueadas;

    /**
     * Fecha y hora de la consulta.
     */
    private LocalDateTime fechaConsulta;

    /**
     * Porcentaje de disponibilidad de la flota.
     */
    private Double porcentajeDisponibilidad;
}
