package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.domain.entity.Aeronave;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Aeronave entity y sus DTOs.
 */
@Mapper(componentModel = "spring", uses = ImagenAeronaveMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AeronaveMapper {

    AeronaveDTO toDTO(Aeronave entity); // Convierte entidad a DTO

    List<AeronaveDTO> toDTOList(List<Aeronave> entities); // Convierte lista de entidades a lista de DTOs

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "historialMantenimiento", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    Aeronave toEntity(AeronaveCreateDTO dto); // Convierte DTO de creación a entidad

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "historialMantenimiento", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AeronaveCreateDTO dto, @MappingTarget Aeronave entity); // Actualiza entidad con datos del DTO de creación

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "modelo", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "historialMantenimiento", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUpdateDTO(AeronaveUpdateDTO dto, @MappingTarget Aeronave entity); // Actualiza entidad con datos del DTO de actualización
}