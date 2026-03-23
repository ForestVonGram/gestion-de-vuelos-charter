package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.DocumentoTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad DocumentoTecnico.
 * Proporciona las operaciones CRUD y las consultas personalizadas mediante Spring Data JPA.
 */
@Repository
public interface DocumentoTecnicoRepository extends JpaRepository<DocumentoTecnico, Long> {

    // Devuelve todos los documentos técnicos asociados a una aeronave específica
    List<DocumentoTecnico> findByAeronaveId(Long aeronaveId);

    // Filtra los documentos técnicos según su categoría (ej. "Manual", "Certificado", etc.)
    List<DocumentoTecnico> findByTipo(String tipo);

    // Busca documentos de un tipo específico que pertenezcan a una aeronave en particular
    List<DocumentoTecnico> findByAeronaveIdAndTipo(Long aeronaveId, String tipo);

    // Devuelve el historial de documentos cargados al sistema por un miembro específico del personal
    List<DocumentoTecnico> findByCargadoPorId(Long personalId);

    // Obtiene todos los documentos de una aeronave ordenados del más reciente al más antiguo
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.aeronave.id = :aeronaveId ORDER BY d.fechaCarga DESC")
    List<DocumentoTecnico> findUltimosDocumentos(@Param("aeronaveId") Long aeronaveId);

    // Filtra exclusivamente los documentos que están marcados como vigentes para una aeronave
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.vigente = true AND d.aeronave.id = :aeronaveId")
    List<DocumentoTecnico> findDocumentosVigentesPorAeronave(@Param("aeronaveId") Long aeronaveId);

    // Filtra exclusivamente los documentos que ya no están vigentes (vencidos/inactivos) para una aeronave
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.vigente = false AND d.aeronave.id = :aeronaveId")
    List<DocumentoTecnico> findDocumentosVencidosPorAeronave(@Param("aeronaveId") Long aeronaveId);

    // Busca documentos cuya fecha de carga se encuentre dentro de un rango de tiempo exacto
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.fechaCarga BETWEEN :inicio AND :fin")
    List<DocumentoTecnico> findByFechaCargaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Identifica los documentos con fecha de caducidad configurada que están próximos a vencer o ya vencieron respecto a la fecha dada
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.fechaVencimiento IS NOT NULL AND d.fechaVencimiento < :fecha")
    List<DocumentoTecnico> findDocumentosProxAVencer(@Param("fecha") LocalDateTime fecha);

    // Busca un documento técnico exacto utilizando su número de identificación oficial o de referencia
    @Query("SELECT d FROM DocumentoTecnico d WHERE d.numeroDocumento = :numeroDocumento")
    DocumentoTecnico findByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    // Devuelve una lista general de documentos en el sistema filtrados por su estado de vigencia
    List<DocumentoTecnico> findByVigente(Boolean vigente);
}