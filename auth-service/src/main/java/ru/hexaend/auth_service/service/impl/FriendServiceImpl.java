package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.entity.FriendRequest;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.repository.FriendRequestRepository;
import ru.hexaend.auth_service.service.interfaces.FriendService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

import ru.hexaend.auth_service.exception.EntityNotFoundException;
import ru.hexaend.auth_service.repository.UserRepository;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserDetailsService userDetailsService;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public Set<User> getFriends() {
        User user = userDetailsService.getCurrentUser();
        return user.getFriends();
    }

    @Override
    @Transactional
    public FriendRequest addFriend(Long userId, String username) {
        User user = userDetailsService.getCurrentUser();
        User friend = userDetailsService.getUserByIdOrUsername(userId, username);

        if (user.equals(friend)) {
            throw new IllegalArgumentException("Cannot add yourself as friend");
        }

        if (user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("User is already a friend");
        }

        FriendRequest friendRequest = FriendRequest.builder()
                .requester(user)
                .recipient(friend)
                .status(FriendRequest.FriendRequestStatus.PENDING)
                .build();

        friendRequestRepository.save(friendRequest);

        return friendRequest;
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, String username) {
        User user = userDetailsService.getCurrentUser();
        User friend = userDetailsService.getUserByIdOrUsername(userId, username);

        user.removeFriend(friend);
        userRepository.save(user);
        userRepository.save(friend);
    }

    @Override
    public List<FriendRequest> getFriendRequests() {
        User user = userDetailsService.getCurrentUser();
        List<FriendRequest> requests = friendRequestRepository.findAllByRecipientAndStatus(user, FriendRequest.FriendRequestStatus.PENDING);
        List<FriendRequest> request2 = friendRequestRepository.findAllByRequesterAndStatus(user, FriendRequest.FriendRequestStatus.PENDING);
        requests.addAll(request2);
        return requests;
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        User currentUser = userDetailsService.getCurrentUser();
        if (!request.getRecipient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to accept this request");
        }

        if (request.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new RuntimeException("Request is not pending");
        }

        request.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        User requester = request.getRequester();
        User recipient = request.getRecipient();

        recipient.addFriend(requester);
        userRepository.save(recipient);
        userRepository.save(requester);
    }

    @Override
    @Transactional
    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        User currentUser = userDetailsService.getCurrentUser();
        if (!request.getRecipient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to decline this request");
        }

        request.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void cancelFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        User currentUser = userDetailsService.getCurrentUser();
        if (!request.getRequester().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to cancel this request");
        }

        request.setStatus(FriendRequest.FriendRequestStatus.CANCELED);
        friendRequestRepository.save(request);
    }
}
