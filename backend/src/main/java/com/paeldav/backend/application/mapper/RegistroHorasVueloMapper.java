package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloCreateDTO;
import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloDTO;
import com.paeldav.backend.domain.entity.RegistroHorasVuelo;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper MapStruct para conversión entre RegistroHorasVuelo y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegistroHorasVueloMapper {

    @Mapping(target = "tripulanteId", source = "tripulante.id")
    @Mapping(target = "tripulanteNombre", expression = "java(mapTripulanteNombre(entity.getTripulante()))")
    @Mapping(target = "vueloId", source = "vuelo.id")
    @Mapping(target = "vueloRuta", expression = "java(mapVueloRuta(entity.getVuelo()))")
    @Mapping(target = "aprobadoPorId", source = "aprobadoPor.id")
    @Mapping(target = "aprobadoPorNombre", expression = "java(mapUsuarioNombre(entity.getAprobadoPor()))")
    RegistroHorasVueloDTO toDTO(RegistroHorasVuelo entity);

    List<RegistroHorasVueloDTO> toDTOList(List<RegistroHorasVuelo> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tripulante", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "aprobado", ignore = true)
    @Mapping(target = "aprobadoPor", ignore = true)
    RegistroHorasVuelo toEntity(RegistroHorasVueloCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tripulante", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "aprobado", ignore = true)
    @Mapping(target = "aprobadoPor", ignore = true)
    void updateEntityFromDTO(RegistroHorasVueloCreateDTO dto, @MappingTarget RegistroHorasVuelo entity);

    default String mapTripulanteNombre(Tripulante tripulante) {
        if (tripulante == null || tripulante.getUsuario() == null) {
            return null;
        }
        return tripulante.getUsuario().getNombre() + " " + tripulante.getUsuario().getApellido();
    }

    default String mapVueloRuta(Vuelo vuelo) {
        if (vuelo == null) {
            return null;
        }
        return vuelo.getOrigen() + " - " + vuelo.getDestino();
    }

    default String mapUsuarioNombre(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return usuario.getNombre() + " " + usuario.getApellido();
    }
}
