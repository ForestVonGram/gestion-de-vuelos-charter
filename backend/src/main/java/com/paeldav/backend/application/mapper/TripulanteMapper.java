package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.tripulante.TripulanteCreateDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Tripulante entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripulanteMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNombre", expression = "java(mapUsuarioNombre(entity.getUsuario()))")
    @Mapping(target = "usuarioEmail", source = "usuario.email")
    TripulanteDTO toDTO(Tripulante entity);

    List<TripulanteDTO> toDTOList(List<Tripulante> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "horasVueloMes", ignore = true)
    @Mapping(target = "estado", constant = "DISPONIBLE")
    Tripulante toEntity(TripulanteCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "horasVueloTotales", ignore = true)
    @Mapping(target = "horasVueloMes", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TripulanteCreateDTO dto, @MappingTarget Tripulante entity);

    default String mapUsuarioNombre(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getNombre() + " " + usuario.getApellido();
    }
}
