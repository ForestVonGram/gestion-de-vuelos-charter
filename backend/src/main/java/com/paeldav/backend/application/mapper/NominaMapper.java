package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.nomina.NominaCreateDTO;
import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.application.dto.nomina.NominaUpdateDTO;
import com.paeldav.backend.domain.entity.Nomina;
import com.paeldav.backend.domain.entity.Personal;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Nomina entity y sus DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NominaMapper {

    @Mapping(target = "personalId", source = "personal.id")
    @Mapping(target = "personalNombre", source = "personal.usuario.nombre")
    @Mapping(target = "personalApellido", source = "personal.usuario.apellido")
    NominaDTO toDTO(Nomina entity);

    List<NominaDTO> toDTOList(List<Nomina> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    Nomina toEntity(NominaCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "mes", ignore = true)
    @Mapping(target = "ano", ignore = true)
    @Mapping(target = "salarioBase", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(NominaUpdateDTO dto, @MappingTarget Nomina entity);
}
