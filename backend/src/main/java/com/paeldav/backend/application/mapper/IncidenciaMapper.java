package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.incidencia.IncidenciaCreateDTO;
import com.paeldav.backend.application.dto.incidencia.IncidenciaDTO;
import com.paeldav.backend.domain.entity.Incidencia;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Incidencia entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IncidenciaMapper {

    @Mapping(target = "vueloId", source = "vuelo.id")
    @Mapping(target = "vueloOrigen", source = "vuelo.origen")
    @Mapping(target = "vueloDestino", source = "vuelo.destino")
    @Mapping(target = "reportadoPorId", source = "reportadoPor.id")
    @Mapping(target = "reportadoPorNombre", expression = "java(mapReportadoPorNombre(entity.getReportadoPor()))")
    @Mapping(target = "resueltoPorId", source = "resueltoPor.id")
    @Mapping(target = "resueltoPorNombre", expression = "java(mapUsuarioNombre(entity.getResueltoPor()))")
    IncidenciaDTO toDTO(Incidencia entity); // Convierte entidad a DTO

    List<IncidenciaDTO> toDTOList(List<Incidencia> entities); // Convierte lista de entidades a lista de DTOs

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    @Mapping(target = "reportadoPor", ignore = true)
    @Mapping(target = "fechaReporte", ignore = true)
    @Mapping(target = "fechaResolucion", ignore = true)
    @Mapping(target = "resuelta", ignore = true)
    @Mapping(target = "accionesTomadas", ignore = true)
    @Mapping(target = "resueltoPor", ignore = true)
    Incidencia toEntity(IncidenciaCreateDTO dto); // Convierte DTO de creación a entidad

    default String mapReportadoPorNombre(Tripulante tripulante) {
        if (tripulante == null || tripulante.getUsuario() == null) return null;
        return tripulante.getUsuario().getNombre() + " " + tripulante.getUsuario().getApellido();
    } // Obtiene nombre completo del tripulante

    default String mapUsuarioNombre(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getNombre() + " " + usuario.getApellido();
    } // Obtiene nombre completo del usuario
}