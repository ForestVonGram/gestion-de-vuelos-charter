package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.HistorialVuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialVueloRepository extends JpaRepository<HistorialVuelo, Long> {

    /**
     * Obtiene el historial de un vuelo ordenado por fecha descendente.
     */
    List<HistorialVuelo> findByVueloIdOrderByFechaCambioDesc(Long vueloId);

    /**
     * Obtiene el historial de un vuelo ordenado por fecha ascendente.
     */
    List<HistorialVuelo> findByVueloIdOrderByFechaCambioAsc(Long vueloId);

    /**
     * Obtiene registros de historial por tipo de acción.
     */
    List<HistorialVuelo> findByTipoAccion(String tipoAccion);

    /**
     * Obtiene historial de cambios a un estado específico.
     */
    List<HistorialVuelo> findByEstadoNuevo(EstadoVuelo estadoNuevo);

    /**
     * Obtiene historial de cambios realizados por un usuario.
     */
    List<HistorialVuelo> findByUsuarioResponsableId(Long usuarioId);

    /**
     * Obtiene historial en un rango de fechas.
     */
    @Query("SELECT h FROM HistorialVuelo h WHERE h.fechaCambio BETWEEN :inicio AND :fin ORDER BY h.fechaCambio DESC")
    List<HistorialVuelo> findByFechaCambioBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Cuenta el número de cambios de estado de un vuelo.
     */
    long countByVueloId(Long vueloId);
}
