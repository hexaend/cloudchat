package ru.hexaend.chat_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "chat_invites")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_invites_gen")
    @SequenceGenerator(name = "chat_invites_gen", sequenceName = "chat_invites_seq", allocationSize = 1)
    @Column(nullable = false)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name="recipient_id", nullable = false)
    private Long recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatInviteStatus status = ChatInviteStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public enum ChatInviteStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELED
    }

}
