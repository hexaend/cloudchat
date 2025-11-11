package ru.hexaend.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toDto(User entity);

    User toEntity(RegisterRequest request);
}
