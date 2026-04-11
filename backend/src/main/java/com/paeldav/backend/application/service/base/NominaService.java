package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.nomina.NominaCreateDTO;
import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.application.dto.nomina.NominaFiltroDTO;
import com.paeldav.backend.application.dto.nomina.NominaUpdateDTO;
import com.paeldav.backend.domain.enums.EstadoNomina;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para gestionar nóminas del personal.
 * Define las operaciones disponibles para crear, actualizar y consultar nóminas.
 */
public interface NominaService {

    /**
     * Genera una nueva nómina para un personal.
     *
     * @param nominaCreateDTO DTO con los datos de la nómina
     * @return NominaDTO con la nómina generada
     */
    NominaDTO generarNomina(NominaCreateDTO nominaCreateDTO);

    /**
     * Obtiene una nómina por su ID.
     *
     * @param id ID de la nómina
     * @return NominaDTO con los datos de la nómina
     */
    NominaDTO obtenerNominaPorId(Long id);

    /**
     * Obtiene todas las nóminas de un personal específico.
     *
     * @param personalId ID del personal
     * @return lista de nóminas del personal
     */
    List<NominaDTO> obtenerNominasPorPersonal(Long personalId);

    /**
     * Obtiene todas las nóminas de un mes y año específicos.
     *
     * @param mes mes de las nóminas
     * @param ano año de las nóminas
     * @return lista de nóminas del período
     */
    List<NominaDTO> obtenerNominasPorPeriodo(Integer mes, Integer ano);

    /**
     * Obtiene una nómina específica de un personal en un mes y año.
     *
     * @param personalId ID del personal
     * @param mes mes de la nómina
     * @param ano año de la nómina
     * @return NominaDTO si existe
     */
    NominaDTO obtenerNominaPorPersonalYPeriodo(Long personalId, Integer mes, Integer ano);

    /**
     * Obtiene todas las nóminas en un estado específico.
     *
     * @param estado estado de la nómina
     * @return lista de nóminas con el estado especificado
     */
    List<NominaDTO> obtenerNominasPorEstado(EstadoNomina estado);

    /**
     * Obtiene todas las nóminas dentro de un rango de fechas.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de nóminas dentro del rango
     */
    List<NominaDTO> obtenerNominasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene todas las nóminas con aplicación de filtros.
     *
     * @param filtro DTO con criterios de filtrado
     * @return lista de nóminas que coinciden con los filtros
     */
    List<NominaDTO> listarNominasConFiltro(NominaFiltroDTO filtro);

    /**
     * Actualiza una nómina existente.
     *
     * @param id ID de la nómina
     * @param nominaUpdateDTO DTO con los datos a actualizar
     * @return NominaDTO con la nómina actualizada
     */
    NominaDTO actualizarNomina(Long id, NominaUpdateDTO nominaUpdateDTO);

    /**
     * Marca una nómina como pagada.
     *
     * @param id ID de la nómina
     * @return NominaDTO con el estado actualizado a PAGADA
     */
    NominaDTO marcarComoPagada(Long id);

    /**
     * Marca una nómina como retenida.
     *
     * @param id ID de la nómina
     * @param motivo motivo de la retención
     * @return NominaDTO con el estado actualizado a RETENIDA
     */
    NominaDTO marcarComoRetenida(Long id, String motivo);

    /**
     * Elimina una nómina por su ID.
     *
     * @param id ID de la nómina a eliminar
     */
    void eliminarNomina(Long id);

    /**
     * Calcula el total de nóminas pagadas para un personal.
     *
     * @param personalId ID del personal
     * @return suma total de nóminas pagadas
     */
    Double calcularTotalNominasPagadas(Long personalId);

    /**
     * Procesa el pago de todas las nóminas pendientes.
     * Este método es típicamente ejecutado por un proceso batch.
     *
     * @return número de nóminas procesadas
     */
    Integer procesarPagosNominaPendientes();

    /**
     * Obtiene el historial de nóminas de un personal.
     *
     * @param personalId ID del personal
     * @return lista de nóminas del personal ordenadas por período
     */
    List<NominaDTO> obtenerHistorialNominas(Long personalId);

    /**
     *Obtiene todas las nominas y la filtra según la pagina, estado, fecha, persona
     */
    Page<NominaDTO> obtenerNominas(Integer page, EstadoNomina estadoNomina, Integer mes, Integer año, Integer personaId );
}
