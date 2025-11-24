package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hexaend.auth_service.entity.FriendRequest;
import ru.hexaend.auth_service.entity.User;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findAllByRecipientAndStatus(User recipient, FriendRequest.FriendRequestStatus status);
    List<FriendRequest> findAllByRequesterAndStatus(User requester, FriendRequest.FriendRequestStatus status);
}