package ru.hexaend.chat_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hexaend.chat_service.entity.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
            SELECT c FROM Chat c
            LEFT JOIN c.participantIds u
            WHERE u = :userId or c.ownerId = :userId
            """)
    List<Chat> findAllByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT c FROM Chat c
            LEFT JOIN c.participantIds u
            WHERE c.type = 'PRIVATE' AND
                  ((c.ownerId = :ownerId AND u = :userId) OR
                   (c.ownerId = :userId AND u = :ownerId))
            """)
    Optional<Chat> findPrivateChatBetweenUsers(Long ownerId, Long userId);
}