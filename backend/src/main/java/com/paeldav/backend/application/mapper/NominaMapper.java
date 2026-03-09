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
 *
 * NOTA IMPORTANTE sobre cálculos automáticos:
 * - totalNeto: Se calcula automáticamente en @PrePersist/@PreUpdate usando la fórmula:
 *   totalNeto = salarioBase + bonificaciones - deducciones
 * - fechaGeneracion: Se asigna automáticamente en @PrePersist
 * - estado: Se inicializa como PENDIENTE por defecto
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NominaMapper {

    @Mapping(target = "personalId", source = "personal.id")
    @Mapping(target = "personalNombre", source = "personal.usuario.nombre")
    @Mapping(target = "personalApellido", source = "personal.usuario.apellido")
    NominaDTO toDTO(Nomina entity); // Convierte entidad a DTO

    List<NominaDTO> toDTOList(List<Nomina> entities); // Convierte lista de entidades a lista de DTOs

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    Nomina toEntity(NominaCreateDTO dto); // Convierte DTO de creación a entidad

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "mes", ignore = true)
    @Mapping(target = "ano", ignore = true)
    @Mapping(target = "salarioBase", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(NominaUpdateDTO dto, @MappingTarget Nomina entity); // Actualiza entidad con datos del DTO
}