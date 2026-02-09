package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveDTO;
import com.paeldav.backend.domain.entity.ImagenAeronave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para convertir entre ImagenAeronave entity y sus DTOs.
 */
@Mapper(componentModel = "spring")
public interface ImagenAeronaveMapper {

    @Mapping(target = "cargadoPorNombre", source = "cargadoPor.usuario.nombre")
    ImagenAeronaveDTO toDTO(ImagenAeronave entity);

    List<ImagenAeronaveDTO> toDTOList(List<ImagenAeronave> entities);
}