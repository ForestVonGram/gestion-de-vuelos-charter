package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroAuditoria;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad RegistroAuditoria.
 * Proporciona métodos para consultar el historial de acciones y eventos de seguridad del sistema.
 */
@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    // Obtiene todo el historial de eventos realizados por un usuario específico
    List<RegistroAuditoria> findByUsuarioId(Long usuarioId);

    // Filtra los registros de auditoría por un tipo de evento en particular (ej. LOGIN, CREAR_AERONAVE, etc.)
    List<RegistroAuditoria> findByTipoEvento(TipoEventoAuditoria tipoEvento);

    // Busca eventos de un tipo específico que hayan sido ejecutados por un usuario en particular
    List<RegistroAuditoria> findByUsuarioIdAndTipoEvento(Long usuarioId, TipoEventoAuditoria tipoEvento);

    // Recupera todos los eventos del sistema que ocurrieron dentro de una ventana de tiempo exacta
    List<RegistroAuditoria> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtiene el rastro de actividad de un usuario específico durante un rango de fechas y horas
    List<RegistroAuditoria> findByUsuarioIdAndTimestampBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    // Busca todas las acciones fallidas o denegadas en el sistema (ej. intentos fallidos de inicio de sesión)
    List<RegistroAuditoria> findByResultadoFalse();

    // Filtra los eventos fallidos por un tipo específico (ej. buscar únicamente intentos fallidos de borrado)
    List<RegistroAuditoria> findByTipoEventoAndResultadoFalse(TipoEventoAuditoria tipoEvento);
}