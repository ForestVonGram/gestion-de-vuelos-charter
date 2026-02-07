package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.repostaje.RepostajeCreateDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.entity.Repostaje;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper MapStruct para conversión entre Repostaje y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepostajeMapper {

    @Mapping(target = "aeronaveId", source = "aeronave.id")
    @Mapping(target = "aeronaveMatricula", source = "aeronave.matricula")
    @Mapping(target = "vueloId", source = "vuelo.id")
    @Mapping(target = "realizadoPorId", source = "realizadoPor.id")
    @Mapping(target = "realizadoPorNombre", expression = "java(mapPersonalNombre(entity.getRealizadoPor()))")
    RepostajeDTO toDTO(Repostaje entity);

    List<RepostajeDTO> toDTOList(List<Repostaje> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    @Mapping(target = "realizadoPor", ignore = true)
    @Mapping(target = "costoTotal", ignore = true)
    Repostaje toEntity(RepostajeCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "vuelo", ignore = true)
    @Mapping(target = "realizadoPor", ignore = true)
    @Mapping(target = "costoTotal", ignore = true)
    void updateEntityFromDTO(RepostajeCreateDTO dto, @MappingTarget Repostaje entity);

    default String mapPersonalNombre(Personal personal) {
        if (personal == null || personal.getUsuario() == null) {
            return null;
        }
        return personal.getUsuario().getNombre() + " " + personal.getUsuario().getApellido();
    }
}
