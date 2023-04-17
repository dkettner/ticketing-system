package com.kett.TicketSystem.notification.repository;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.notification.domain.consumedData.UserDataOfNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDataOfNotificationRepository extends JpaRepository<UserDataOfNotification, UUID> {
    List<UserDataOfNotification> findByUserId(UUID userId);
    List<UserDataOfNotification> findByUserEmailEquals(EmailAddress emailAddress);
    Integer deleteByUserId(UUID userId);
}
