package ru.hexaend.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.userdetails.UserDetails;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.dto.response.UserWithVerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.security.UserPrincipal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toDto(User entity);

    @Mapping(target = "isVerified", source = "entity.emailVerified")
    UserWithVerifyStatusResponse toDtoWithVerifyStatus(User entity);

    User toEntity(RegisterRequest request);

    UserPrincipal toUserDetails(User entity);
}
