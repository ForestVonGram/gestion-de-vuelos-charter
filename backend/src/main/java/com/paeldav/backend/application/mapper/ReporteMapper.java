package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.domain.entity.Reporte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper MapStruct para convertir entre la entidad Reporte y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReporteMapper {

    /**
     * Convierte una entidad Reporte a ReporteDTO.
     * Mapea el nombre del usuario que generó el reporte.
     */
    @Mapping(source = "generadoPor.nombre", target = "generadoPorNombre")
    ReporteDTO toDTO(Reporte reporte);

    /**
     * Convierte un ReporteDTO a entidad Reporte.
     * Nota: No mapea el usuario, debe establecerse manualmente en el servicio.
     */
    @Mapping(target = "generadoPor", ignore = true)
    Reporte toEntity(ReporteDTO reporteDTO);
}
