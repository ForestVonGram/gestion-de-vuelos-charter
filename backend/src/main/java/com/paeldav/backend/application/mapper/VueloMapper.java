package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.dto.vuelo.VueloUpdateDTO;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Vuelo entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VueloMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNombre", expression = "java(mapUsuarioNombre(entity.getUsuario()))")
    @Mapping(target = "aeronaveId", source = "aeronave.id")
    @Mapping(target = "aeronaveMatricula", source = "aeronave.matricula")
    @Mapping(target = "tripulacionIds", expression = "java(mapTripulacionIds(entity.getTripulacion()))")
    VueloDTO toDTO(Vuelo entity);

    List<VueloDTO> toDTOList(List<Vuelo> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "tripulacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaSolicitud", ignore = true)
    @Mapping(target = "fechaSalidaReal", ignore = true)
    @Mapping(target = "fechaLlegadaReal", ignore = true)
    @Mapping(target = "costoEstimado", ignore = true)
    @Mapping(target = "incidencias", ignore = true)
    @Mapping(target = "pasajeros", ignore = true)
    @Mapping(target = "registrosHoras", ignore = true)
    @Mapping(target = "repostajes", ignore = true)
    Vuelo toEntity(VueloCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "tripulacion", ignore = true)
    @Mapping(target = "fechaSolicitud", ignore = true)
    @Mapping(target = "incidencias", ignore = true)
    @Mapping(target = "pasajeros", ignore = true)
    @Mapping(target = "registrosHoras", ignore = true)
    @Mapping(target = "repostajes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(VueloUpdateDTO dto, @MappingTarget Vuelo entity);

    default String mapUsuarioNombre(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getNombre() + " " + usuario.getApellido();
    }

    default List<Long> mapTripulacionIds(List<Tripulante> tripulacion) {
        if (tripulacion == null) return null;
        return tripulacion.stream().map(Tripulante::getId).toList();
    }
}
