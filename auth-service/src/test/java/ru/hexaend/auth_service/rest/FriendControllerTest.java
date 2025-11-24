package ru.hexaend.auth_service.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hexaend.auth_service.dto.response.FriendRequestResponse;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.entity.FriendRequest;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.mapper.FriendRequestMapper;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.service.interfaces.FriendService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private FriendRequestMapper friendRequestMapper;

    @Test
    @DisplayName("GET /friends returns list of friends")
    void getFriendsReturnsListOfFriends() throws Exception {
        User friend = new User();
        friend.setId(2L);
        friend.setUsername("friend");

        UserResponse userResponse = new UserResponse(2L, "friend", "friend@test.com", "Friend", "User");

        when(friendService.getFriends()).thenReturn(Set.of(friend));
        when(userMapper.toDto(friend)).thenReturn(userResponse);

        mockMvc.perform(get("/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("friend")));

        verify(friendService).getFriends();
        verify(userMapper).toDto(friend);
    }

    @Test
    @DisplayName("POST /friends adds a friend request")
    void addFriendCreatesRequest() throws Exception {
        Long friendId = 2L;
        String friendUsername = "friend";
        FriendRequest request = new FriendRequest();
        request.setId(1L);

        FriendRequestResponse response = new FriendRequestResponse(1L, null, null, "PENDING");

        when(friendService.addFriend(friendId, friendUsername)).thenReturn(request);
        when(friendRequestMapper.toResponse(request)).thenReturn(response);

        mockMvc.perform(post("/friends")
                .param("id", String.valueOf(friendId))
                .param("username", friendUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(friendService).addFriend(friendId, friendUsername);
        verify(friendRequestMapper).toResponse(request);
    }

    @Test
    @DisplayName("DELETE /friends removes a friend")
    void removeFriendRemovesFriend() throws Exception {
        Long friendId = 2L;
        String friendUsername = "friend";

        mockMvc.perform(delete("/friends")
                .param("id", String.valueOf(friendId))
                .param("username", friendUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Remove friend")));

        verify(friendService).removeFriend(friendId, friendUsername);
    }

    @Test
    @DisplayName("GET /friends/requests returns list of requests")
    void getFriendRequestsReturnsList() throws Exception {
        FriendRequest request = new FriendRequest();
        FriendRequestResponse response = new FriendRequestResponse(1L, null, null, "PENDING");

        when(friendService.getFriendRequests()).thenReturn(List.of(request));
        when(friendRequestMapper.toResponse(request)).thenReturn(response);

        mockMvc.perform(get("/friends/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));

        verify(friendService).getFriendRequests();
    }

    @Test
    @DisplayName("POST /friends/requests/accept accepts request")
    void acceptFriendRequestAccepts() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(post("/friends/requests/accept")
                .param("id", String.valueOf(requestId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Accept friend request")));

        verify(friendService).acceptFriendRequest(requestId);
    }

    @Test
    @DisplayName("POST /friends/requests/decline declines request")
    void declineFriendRequestDeclines() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(post("/friends/requests/decline")
                .param("id", String.valueOf(requestId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Decline friend request")));

        verify(friendService).declineFriendRequest(requestId);
    }

    @Test
    @DisplayName("PATCH /friends/requests/cancel cancels request")
    void cancelFriendRequestCancels() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(patch("/friends/requests/cancel")
                .param("id", String.valueOf(requestId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Cancel friend request")));

        verify(friendService).cancelFriendRequest(requestId);
    }
}
