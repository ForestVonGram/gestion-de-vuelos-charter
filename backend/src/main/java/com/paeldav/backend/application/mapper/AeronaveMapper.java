package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.domain.entity.Aeronave;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Aeronave entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AeronaveMapper {

    AeronaveDTO toDTO(Aeronave entity);

    List<AeronaveDTO> toDTOList(List<Aeronave> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "historialMantenimiento", ignore = true)
    Aeronave toEntity(AeronaveCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "historialMantenimiento", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AeronaveCreateDTO dto, @MappingTarget Aeronave entity);
}
