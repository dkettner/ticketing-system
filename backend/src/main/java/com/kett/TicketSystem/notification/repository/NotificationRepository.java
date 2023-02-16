package com.kett.TicketSystem.notification.repository;

import com.kett.TicketSystem.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientId(UUID recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(UUID recipientId);
    Long removeById(UUID id);
    void deleteByRecipientId(UUID recipientId);
}
