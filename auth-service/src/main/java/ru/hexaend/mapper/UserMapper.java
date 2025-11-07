package ru.hexaend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.hexaend.dto.request.RegisterRequest;
import ru.hexaend.dto.response.UserResponse;
import ru.hexaend.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toDto(User entity);

    User toEntity(RegisterRequest request);
}
