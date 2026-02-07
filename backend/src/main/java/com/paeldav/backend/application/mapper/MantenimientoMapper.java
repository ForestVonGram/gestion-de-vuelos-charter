package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Mantenimiento entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MantenimientoMapper {

    @Mapping(target = "aeronaveId", source = "aeronave.id")
    @Mapping(target = "aeronaveMatricula", source = "aeronave.matricula")
    @Mapping(target = "responsableId", source = "responsable.id")
    @Mapping(target = "responsableNombre", expression = "java(mapResponsableNombre(entity.getResponsable()))")
    MantenimientoDTO toDTO(Mantenimiento entity);

    List<MantenimientoDTO> toDTOList(List<Mantenimiento> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    @Mapping(target = "fechaFin", ignore = true)
    @Mapping(target = "completado", ignore = true)
    Mantenimiento toEntity(MantenimientoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    @Mapping(target = "fechaFin", ignore = true)
    @Mapping(target = "completado", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MantenimientoCreateDTO dto, @MappingTarget Mantenimiento entity);

    default String mapResponsableNombre(Usuario responsable) {
        if (responsable == null) return null;
        return responsable.getNombre() + " " + responsable.getApellido();
    }
}
