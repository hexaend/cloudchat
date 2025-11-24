package ru.hexaend.auth_service.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.auth_service.dto.response.FriendRequestResponse;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.entity.FriendRequest;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.mapper.FriendRequestMapper;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.service.interfaces.FriendService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friends", description = "Endpoints for managing friends and friend requests")
public class FriendController {

    private final FriendService friendService;
    private final UserMapper userMapper;
    private final FriendRequestMapper friendRequestMapper;

    @Operation(summary = "Get friends list", description = "Retrieve a list of all friends")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved friends list", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public ResponseEntity<List<UserResponse>> getFriends() {
        Set<User> users = friendService.getFriends();
        List<UserResponse> list = users.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Send friend request", description = "Send a friend request to another user by ID or username")
    @ApiResponse(responseCode = "200", description = "Friend request sent successfully", content = @Content(schema = @Schema(implementation = FriendRequestResponse.class)))
    @PostMapping
    public ResponseEntity<FriendRequestResponse> addFriend(
            @Parameter(description = "User ID to add as friend") @RequestParam(value = "id", required = false) Long userId,
            @Parameter(description = "Username to add as friend") @RequestParam(value = "username", required = false) String username) {
        FriendRequest friendRequest = friendService.addFriend(userId, username);
        FriendRequestResponse response = friendRequestMapper.toResponse(friendRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove friend", description = "Remove a user from friends list")
    @ApiResponse(responseCode = "200", description = "Friend removed successfully")
    @DeleteMapping
    public ResponseEntity<String> removeFriend(
            @Parameter(description = "User ID to remove") @RequestParam(value = "id", required = false) Long userId,
            @Parameter(description = "Username to remove") @RequestParam(value = "username", required = false) String username) {
        friendService.removeFriend(userId, username);
        return ResponseEntity.ok("Remove friend");
    }

    @Operation(summary = "Get friend requests", description = "Retrieve a list of incoming friend requests")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved friend requests", content = @Content(schema = @Schema(implementation = FriendRequestResponse.class)))
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getFriendRequests() {
        List<FriendRequest> requests = friendService.getFriendRequests();
        List<FriendRequestResponse> response = requests.stream()
                .map(friendRequestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Accept friend request", description = "Accept an incoming friend request")
    @ApiResponse(responseCode = "200", description = "Friend request accepted")
    @PostMapping("/requests/accept")
    public ResponseEntity<String> acceptFriendRequest(@Parameter(description = "Request ID") @RequestParam("id") Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Accept friend request");
    }

    @Operation(summary = "Decline friend request", description = "Decline an incoming friend request")
    @ApiResponse(responseCode = "200", description = "Friend request declined")
    @PostMapping("/requests/decline")
    public ResponseEntity<String> declineFriendRequest(@Parameter(description = "Request ID") @RequestParam("id") Long requestId) {
        friendService.declineFriendRequest(requestId);
        return ResponseEntity.ok("Decline friend request");
    }

    @Operation(summary = "Cancel friend request", description = "Cancel an outgoing friend request")
    @ApiResponse(responseCode = "200", description = "Friend request canceled")
    @PatchMapping("/requests/cancel")
    public ResponseEntity<String> cancelFriendRequest(@Parameter(description = "Request ID") @RequestParam("id") Long requestId) {
        friendService.cancelFriendRequest(requestId);
        return ResponseEntity.ok("Cancel friend request");
    }

}
