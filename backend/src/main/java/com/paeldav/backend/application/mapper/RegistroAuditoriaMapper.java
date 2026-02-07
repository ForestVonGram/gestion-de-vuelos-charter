package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.registroauditoria.RegistroAuditoriaDTO;
import com.paeldav.backend.domain.entity.RegistroAuditoria;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre RegistroAuditoria entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegistroAuditoriaMapper {

    RegistroAuditoriaDTO toDTO(RegistroAuditoria entity);

    List<RegistroAuditoriaDTO> toDTOList(List<RegistroAuditoria> entities);

    RegistroAuditoria toEntity(RegistroAuditoriaDTO dto);
}
