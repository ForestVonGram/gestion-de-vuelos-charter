package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByVueloId(Long vueloId);

    List<Incidencia> findByReportadaPorId(Long tripulanteId);

    List<Incidencia> findByResuelta(Boolean resuelta);

    @Query("SELECT i FROM Incidencia i WHERE i.fechaReporte BETWEEN :inicio AND :fin")
    List<Incidencia> findByFechaReporteBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Incidencia i WHERE i.resuelta = false ORDER BY i.fechaReporte DESC")
    List<Incidencia> findPendientes();

    List<Incidencia> findByTipoIncidencia(String tipoIncidencia);
}
