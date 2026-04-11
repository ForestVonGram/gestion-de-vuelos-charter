package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.domain.entity.Nomina;
import com.paeldav.backend.domain.enums.EstadoNomina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Nómina.
 * Proporciona métodos para acceder y manipular datos de nóminas en la base de datos.
 */
@Repository
public interface NominaRepository extends JpaRepository<Nomina, Long> {

    /**
     * Encuentra una nómina específica por personal, mes y año.
     *
     * @param personalId ID del personal
     * @param mes mes de la nómina
     * @param ano año de la nómina
     * @return Optional con la nómina si existe
     */
    Optional<Nomina> findByPersonalIdAndMesAndAno(Long personalId, Integer mes, Integer ano);

    /**
     * Encuentra todas las nóminas de un personal específico.
     *
     * @param personalId ID del personal
     * @return lista de nóminas del personal
     */
    List<Nomina> findByPersonalIdOrderByAnoDescMesDesc(Long personalId);

    /**
     * Encuentra todas las nóminas en un estado específico.
     *
     * @param estado estado de la nómina
     * @return lista de nóminas con el estado especificado
     */
    List<Nomina> findByEstado(EstadoNomina estado);

    /**
     * Encuentra todas las nóminas de un mes y año específicos.
     *
     * @param mes mes de las nóminas
     * @param ano año de las nóminas
     * @return lista de nóminas del período
     */
    List<Nomina> findByMesAndAno(Integer mes, Integer ano);

    /**
     * Encuentra todas las nóminas de un período de fechas.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de nóminas dentro del rango de fechas
     */
    @Query("SELECT n FROM Nomina n WHERE n.fechaGeneracion BETWEEN :fechaInicio AND :fechaFin ORDER BY n.fechaGeneracion DESC")
    List<Nomina> findByFechaGeneracionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Calcula el total de nóminas pagadas para un personal.
     *
     * @param personalId ID del personal
     * @return suma total de nóminas pagadas
     */
    @Query("SELECT COALESCE(SUM(n.totalNeto), 0) FROM Nomina n WHERE n.personal.id = :personalId AND n.estado = 'PAGADA'")
    Double sumTotalNetoByPersonalIdAndEstadoPagada(@Param("personalId") Long personalId);

    /**
     * Cuenta las nóminas pendientes de pago.
     *
     * @return cantidad de nóminas pendientes
     */
    Long countByEstado(EstadoNomina estado);

    /**
     * Encuentra todas las nóminas de un personal en un período específico.
     *
     * @param personalId ID del personal
     * @param mesInicio mes de inicio (inclusive)
     * @param anoInicio año de inicio (inclusive)
     * @param mesFin mes de fin (inclusive)
     * @param anoFin año de fin (inclusive)
     * @return lista de nóminas en el período
     */
    @Query("SELECT n FROM Nomina n WHERE n.personal.id = :personalId AND " +
            "((n.ano > :anoInicio) OR (n.ano = :anoInicio AND n.mes >= :mesInicio)) AND " +
            "((n.ano < :anoFin) OR (n.ano = :anoFin AND n.mes <= :mesFin)) " +
            "ORDER BY n.ano DESC, n.mes DESC")
    List<Nomina> findByPersonalIdAndPeriodo(
            @Param("personalId") Long personalId,
            @Param("mesInicio") Integer mesInicio,
            @Param("anoInicio") Integer anoInicio,
            @Param("mesFin") Integer mesFin,
            @Param("anoFin") Integer anoFin
    );



    @Query("""
        SELECT n FROM Nomina n
        WHERE (:estadoNomina IS NULL OR n.estado = :estadoNomina)
        AND (:mes IS NULL OR n.mes = :mes)
        AND (:anio IS NULL OR n.ano = :anio)
        AND (:personaId IS NULL OR n.personal.id = :personaId)
        """)
    Page<Nomina> findByFiltros(
            @Param("estadoNomina") EstadoNomina estadoNomina,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio,
            @Param("personaId") Integer personaId,
            Pageable pageable
    );

}
