package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroAuditoria;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    List<RegistroAuditoria> findByUsuarioId(Long usuarioId);

    List<RegistroAuditoria> findByTipoEvento(TipoEventoAuditoria tipoEvento);

    List<RegistroAuditoria> findByUsuarioIdAndTipoEvento(Long usuarioId, TipoEventoAuditoria tipoEvento);

    List<RegistroAuditoria> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    List<RegistroAuditoria> findByUsuarioIdAndTimestampBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    List<RegistroAuditoria> findByResultadoFalse();

    List<RegistroAuditoria> findByTipoEventoAndResultadoFalse(TipoEventoAuditoria tipoEvento);
}
