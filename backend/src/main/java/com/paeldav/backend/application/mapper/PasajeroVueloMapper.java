package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.pasajerovuelo.PasajeroVueloCreateDTO;
import com.paeldav.backend.application.dto.pasajerovuelo.PasajeroVueloDTO;
import com.paeldav.backend.domain.entity.PasajeroVuelo;
import com.paeldav.backend.domain.entity.Vuelo;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper MapStruct para conversión entre PasajeroVuelo y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PasajeroVueloMapper {

    @Mapping(target = "vueloId", source = "vuelo.id")
    @Mapping(target = "nombreCompleto", expression = "java(entity.getNombreCompleto())")
    PasajeroVueloDTO toDTO(PasajeroVuelo entity); // Convierte entidad a DTO

    List<PasajeroVueloDTO> toDTOList(List<PasajeroVuelo> entities); // Convierte lista de entidades a lista de DTOs

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    PasajeroVuelo toEntity(PasajeroVueloCreateDTO dto); // Convierte DTO de creación a entidad

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    void updateEntityFromDTO(PasajeroVueloCreateDTO dto, @MappingTarget PasajeroVuelo entity); // Actualiza entidad con datos del DTO

    default Long mapVueloToId(Vuelo vuelo) {
        return vuelo != null ? vuelo.getId() : null;
    } // Obtiene ID del vuelo
}