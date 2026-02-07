package com.paeldav.backend.application.mapper;

import com.paeldav.backend.application.dto.rol.UserRoleInfoDTO;
import com.paeldav.backend.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper para conversión de entidades Usuario a DTOs de rol.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    /**
     * Convierte una entidad Usuario a UserRoleInfoDTO.
     *
     * @param usuario la entidad Usuario
     * @return el DTO con información de rol del usuario
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "telefono", source = "telefono")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "activo", source = "activo")
    @Mapping(target = "fechaRegistro", source = "fechaRegistro")
    UserRoleInfoDTO toUserRoleInfoDTO(Usuario usuario);

    /**
     * Convierte una lista de usuarios a lista de UserRoleInfoDTO.
     *
     * @param usuarios lista de entidades Usuario
     * @return lista de DTOs
     */
    List<UserRoleInfoDTO> toUserRoleInfoDTOList(List<Usuario> usuarios);
}
