package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.vuelo.HistorialVueloDTO;
import com.paeldav.backend.domain.entity.HistorialVuelo;
import com.paeldav.backend.domain.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre HistorialVuelo entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HistorialVueloMapper {

    @Mapping(target = "vueloId", source = "vuelo.id")
    @Mapping(target = "usuarioResponsableId", source = "usuarioResponsable.id")
    @Mapping(target = "usuarioResponsableNombre", expression = "java(mapUsuarioNombre(entity.getUsuarioResponsable()))")
    HistorialVueloDTO toDTO(HistorialVuelo entity);

    List<HistorialVueloDTO> toDTOList(List<HistorialVuelo> entities);

    default String mapUsuarioNombre(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getNombre() + " " + usuario.getApellido();
    }
}
