package ru.hexaend.chat_service.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.chat_service.dto.response.ChatResponse;
import ru.hexaend.chat_service.entity.Chat;
import ru.hexaend.chat_service.mapper.ChatMapper;
import ru.hexaend.chat_service.repository.ChatRepository;
import ru.hexaend.chat_service.service.interfaces.ChatService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Chats", description = "Endpoints for managing chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Operation(summary = "Get all chats", description = "Retrieve a list of all chats for the current user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved chats",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatResponse.class))))
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        List<ChatResponse> chatResponses = chats.stream().map(chatMapper::toResponse).toList();
        return ResponseEntity.ok(chatResponses);
    }

    @Operation(summary = "Get chat by ID", description = "Retrieve details of a specific chat by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved chat",
            content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ChatResponse> getChatById(@Parameter(description = "Chat ID") @PathVariable Long id) {
        Chat chat = chatService.getChatById(id);
        ChatResponse chatResponse = chatMapper.toResponse(chat);
        return ResponseEntity.ok(chatResponse);
    }

    @Operation(summary = "Get or create dialogue", description = "Get existing dialogue with a user or create a new one")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved or created dialogue",
            content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    @PostMapping("/dialogue/{userId}")
    public ResponseEntity<ChatResponse> getOrCreateDialogue(@Parameter(description = "User ID to create dialogue with") @PathVariable Long userId) {
        Chat chat = chatService.getOrCreateDialogue(userId);
        ChatResponse chatResponse = chatMapper.toResponse(chat);
        return ResponseEntity.ok(chatResponse);
    }

    @Operation(summary = "Create group chat", description = "Create a new group chat with a name")
    @ApiResponse(responseCode = "200", description = "Successfully created group chat",
            content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createChat(@Parameter(description = "Name of the group chat") @RequestParam String name) {

        Chat chat = chatService.createGroupChat(name);
        ChatResponse chatResponse = chatMapper.toResponse(chat);
        return ResponseEntity.ok(chatResponse);
    }

//    @PatchMapping
//    public ResponseEntity<?> updateChat() {
//        return null;
//    }

//    @DeleteMapping
//    public ResponseEntity<?> deleteChat() {
//        return null;
//    }

    @Operation(summary = "Add participant", description = "Add a user to the chat")
    @ApiResponse(responseCode = "200", description = "Successfully added participant")
    @PutMapping("/{id}/participants")
    public ResponseEntity<Void> addParticipant(
            @Parameter(description = "Chat ID") @PathVariable("id") Long chatId,
            @Parameter(description = "User ID to add") @RequestParam(required = false) Long userId,
            @Parameter(description = "Username to add") @RequestParam(required = false) String username) {

        chatService.addParticipant(chatId, userId, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove participant", description = "Remove a user from the chat")
    @ApiResponse(responseCode = "200", description = "Successfully removed participant")
    @DeleteMapping("/{id}/participants")
    public ResponseEntity<Void> removeParticipant(
            @Parameter(description = "Chat ID") @PathVariable("id") Long chatId,
            @Parameter(description = "User ID to remove") @RequestParam(required = false) Long userId,
            @Parameter(description = "Username to remove") @RequestParam(required = false) String username) {
        chatService.removeParticipant(chatId, userId, username);
        return ResponseEntity.ok().build();
    }
}
