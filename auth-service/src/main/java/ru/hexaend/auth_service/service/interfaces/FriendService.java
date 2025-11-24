package ru.hexaend.auth_service.service.interfaces;

import ru.hexaend.auth_service.entity.FriendRequest;
import ru.hexaend.auth_service.entity.User;

import java.util.List;
import java.util.Set;

public interface FriendService {

    Set<User> getFriends();

    FriendRequest addFriend(Long userId, String username);

    void removeFriend(Long userId, String username);

    List<FriendRequest> getFriendRequests();

    void acceptFriendRequest(Long requestId);

    void declineFriendRequest(Long requestId);

    void cancelFriendRequest(Long requestId);
}
