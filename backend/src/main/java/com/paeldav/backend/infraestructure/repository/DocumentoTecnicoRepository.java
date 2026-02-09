package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.DocumentoTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentoTecnicoRepository extends JpaRepository<DocumentoTecnico, Long> {

    List<DocumentoTecnico> findByAeronaveId(Long aeronaveId);

    List<DocumentoTecnico> findByTipo(String tipo);

    List<DocumentoTecnico> findByAeronaveIdAndTipo(Long aeronaveId, String tipo);

    List<DocumentoTecnico> findByCargadoPorId(Long personalId);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.aeronave.id = :aeronaveId ORDER BY d.fechaCarga DESC")
    List<DocumentoTecnico> findUltimosDocumentos(@Param("aeronaveId") Long aeronaveId);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.vigente = true AND d.aeronave.id = :aeronaveId")
    List<DocumentoTecnico> findDocumentosVigentesPorAeronave(@Param("aeronaveId") Long aeronaveId);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.vigente = false AND d.aeronave.id = :aeronaveId")
    List<DocumentoTecnico> findDocumentosVencidosPorAeronave(@Param("aeronaveId") Long aeronaveId);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.fechaCarga BETWEEN :inicio AND :fin")
    List<DocumentoTecnico> findByFechaCargaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.fechaVencimiento IS NOT NULL AND d.fechaVencimiento < :fecha")
    List<DocumentoTecnico> findDocumentosProxAVencer(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT d FROM DocumentoTecnico d WHERE d.numeroDocumento = :numeroDocumento")
    DocumentoTecnico findByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    List<DocumentoTecnico> findByVigente(Boolean vigente);
}
