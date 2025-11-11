package ru.hexaend.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.hexaend.auth_service.dto.response.RoleResponse;
import ru.hexaend.auth_service.entity.Role;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

    RoleResponse toResponse(Role role);

}
