package com.kett.TicketSystem.notification.application;

import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.notification.domain.Notification;
import com.kett.TicketSystem.notification.domain.exceptions.NoNotificationFoundException;
import com.kett.TicketSystem.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification getNotificationById(UUID id) throws NoNotificationFoundException {
        return notificationRepository
                .findById(id)
                .orElseThrow(() -> new NoNotificationFoundException("could not find notification with id: " + id));
    }

    public List<Notification> getNotificationsByRecipientId(UUID recipientId) throws NoNotificationFoundException {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        if (notifications.isEmpty()) {
            throw new NoNotificationFoundException("could not find notifications with recipientId: " + recipientId);
        }
        return notifications;
    }

    public List<Notification> getUnreadNotificationsByRecipientId(UUID recipientId) throws NoNotificationFoundException {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalse(recipientId);
        if (notifications.isEmpty()) {
            throw new NoNotificationFoundException("could not find notifications with recipientId: " + recipientId);
        }
        return notifications;
    }

    public UUID getGetRecipientIdByNotificationId(UUID id) throws NoNotificationFoundException {
        return this
                .getNotificationById(id)
                .getRecipientId();
    }

    @EventListener
    @Async
    public void handleUnacceptedProjectMembershipCreatedEvent(UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent) {
        String message = "You got invited to project " + unacceptedProjectMembershipCreatedEvent.getProjectId();

        Notification notification = new Notification(unacceptedProjectMembershipCreatedEvent.getInviteeId(), message);
        notificationRepository.save(notification);
    }
}
