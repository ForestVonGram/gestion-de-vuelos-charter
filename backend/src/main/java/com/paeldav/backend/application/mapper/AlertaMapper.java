package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.alerta.AlertaCreateDTO;
import com.paeldav.backend.application.dto.alerta.AlertaDTO;
import com.paeldav.backend.domain.entity.Alerta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper para convertir entre Alerta, AlertaDTO y AlertaCreateDTO.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlertaMapper {

    @Mapping(source = "aeronave.id", target = "aeronaveId")
    @Mapping(source = "aeronave.matricula", target = "aeronaveMatricula")
    @Mapping(source = "mantenimientoRelacionado.id", target = "mantenimientoRelacionadoId")
    AlertaDTO toDTO(Alerta alerta);

    List<AlertaDTO> toDTOList(List<Alerta> alertas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "mantenimientoRelacionado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaResolucion", ignore = true)
    Alerta toEntity(AlertaCreateDTO alertaCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "mantenimientoRelacionado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaResolucion", ignore = true)
    void updateEntityFromDTO(AlertaCreateDTO alertaCreateDTO, @MappingTarget Alerta alerta);
}
