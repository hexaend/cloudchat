package ru.hexaend.chat_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.hexaend.chat_service.dto.response.ChatResponse;
import ru.hexaend.chat_service.entity.Chat;

@Mapper(componentModel= MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {

    ChatResponse toResponse(Chat chat);

}
