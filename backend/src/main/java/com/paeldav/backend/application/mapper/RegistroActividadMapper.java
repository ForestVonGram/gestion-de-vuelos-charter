package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.registroactividad.RegistroActividadDTO;
import com.paeldav.backend.domain.entity.RegistroActividad;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre RegistroActividad entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegistroActividadMapper {

    RegistroActividadDTO toDTO(RegistroActividad entity);

    List<RegistroActividadDTO> toDTOList(List<RegistroActividad> entities);

    RegistroActividad toEntity(RegistroActividadDTO dto);
}
