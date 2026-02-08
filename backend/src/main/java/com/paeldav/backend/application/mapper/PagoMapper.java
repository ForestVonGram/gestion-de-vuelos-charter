package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.pago.PagoDTO;
import com.paeldav.backend.domain.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper para convertir entre entidad Pago y DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PagoMapper {

    /**
     * Convierte una entidad Pago a PagoDTO.
     *
     * @param pago entidad a convertir
     * @return DTO convertido
     */
    @Mapping(source = "vuelo.id", target = "vueloId")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    PagoDTO toDTO(Pago pago);

    /**
     * Convierte una lista de entidades Pago a lista de PagoDTO.
     *
     * @param pagos lista de entidades a convertir
     * @return lista de DTOs convertidos
     */
    List<PagoDTO> toDTOList(List<Pago> pagos);
}
