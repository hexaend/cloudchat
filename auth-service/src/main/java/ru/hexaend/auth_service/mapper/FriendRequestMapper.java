package ru.hexaend.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.hexaend.auth_service.dto.response.FriendRequestResponse;
import ru.hexaend.auth_service.entity.FriendRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FriendRequestMapper {

    @Mapping(target = "sender", source = "friendRequest.requester")
    @Mapping(target = "receiver", source = "friendRequest.recipient")
    FriendRequestResponse toResponse(FriendRequest friendRequest);

}
