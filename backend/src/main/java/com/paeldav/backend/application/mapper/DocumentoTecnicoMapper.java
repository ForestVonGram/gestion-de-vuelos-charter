package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoCreateDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoUpdateDTO;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.DocumentoTecnico;
import com.paeldav.backend.domain.entity.Personal;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper MapStruct para conversión entre DocumentoTecnico y sus DTOs.
 * 
 * NOTA IMPORTANTE sobre cálculos automáticos:
 * - vigente: Se asigna automáticamente como true en @PrePersist de la entidad
 * - fechaCarga: Se asigna automáticamente en @PrePersist si es null
 * - vigente se recalcula en @PreUpdate basado en fechaVencimiento
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentoTecnicoMapper {

    @Mapping(target = "aeronaveId", source = "aeronave.id")
    @Mapping(target = "aeronaveMatricula", source = "aeronave.matricula")
    @Mapping(target = "cargadoPorId", source = "cargadoPor.id")
    @Mapping(target = "cargadoPorNombre", expression = "java(mapPersonalNombre(entity.getCargadoPor()))")
    DocumentoTecnicoDTO toDTO(DocumentoTecnico entity);

    List<DocumentoTecnicoDTO> toDTOList(List<DocumentoTecnico> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "cargadoPor", ignore = true)
    @Mapping(target = "urlDocumento", ignore = true)
    @Mapping(target = "idCloudinary", ignore = true)
    @Mapping(target = "fechaCarga", ignore = true)
    @Mapping(target = "tamañoBytes", ignore = true)
    @Mapping(target = "tipoArchivo", ignore = true)
    @Mapping(target = "vigente", constant = "true")  // Asignado automáticamente en @PrePersist
    DocumentoTecnico toEntity(DocumentoTecnicoCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aeronave", ignore = true)
    @Mapping(target = "cargadoPor", ignore = true)
    @Mapping(target = "urlDocumento", ignore = true)
    @Mapping(target = "idCloudinary", ignore = true)
    @Mapping(target = "fechaCarga", ignore = true)
    @Mapping(target = "tamañoBytes", ignore = true)
    @Mapping(target = "tipoArchivo", ignore = true)
    void updateEntityFromDTO(DocumentoTecnicoUpdateDTO dto, @MappingTarget DocumentoTecnico entity);

    default String mapPersonalNombre(Personal personal) {
        if (personal == null || personal.getUsuario() == null) {
            return null;
        }
        return personal.getUsuario().getNombre() + " " + personal.getUsuario().getApellido();
    }
}
