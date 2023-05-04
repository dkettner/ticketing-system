package com.kett.TicketSystem.notification.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.notification.domain.consumedData.UserDataOfNotification;
import com.kett.TicketSystem.notification.domain.exceptions.NoNotificationFoundException;
import com.kett.TicketSystem.notification.domain.exceptions.NotificationException;
import com.kett.TicketSystem.notification.repository.NotificationRepository;
import com.kett.TicketSystem.notification.repository.UserDataOfNotificationRepository;
import com.kett.TicketSystem.ticket.domain.events.TicketAssignedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketUnassignedEvent;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationDomainService {
    private final NotificationRepository notificationRepository;
    private final UserDataOfNotificationRepository userDataOfNotificationRepository;

    @Autowired
    public NotificationDomainService(
            NotificationRepository notificationRepository,
            UserDataOfNotificationRepository userDataOfNotificationRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userDataOfNotificationRepository = userDataOfNotificationRepository;
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

    public List<Notification> getNotificationsByUserEmail(EmailAddress emailAddress) throws NotificationException {
        return getNotificationsByRecipientId(
                        getUserIdByUserEmailAddress(emailAddress)
                );
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

    public UUID getUserIdByUserEmailAddress(EmailAddress emailAddress) {
        List<UserDataOfNotification> userData = userDataOfNotificationRepository.findByUserEmailEquals(emailAddress);
        if (userData.isEmpty()) {
            throw new ImpossibleException("no user data found for user: " + emailAddress.toString());
        }
        return userData.get(0).getUserId();
    }

    public void patchById(UUID id, Boolean isRead) throws NoNotificationFoundException, NotificationException, IllegalStateUpdateException {
        Notification notification = this.getNotificationById(id);
        notification.setIsRead(isRead);
        notificationRepository.save(notification);
    }

    public void deleteById(UUID id) throws NoNotificationFoundException {
        Long numOfDeletedNotifications = notificationRepository.removeById(id);
        if (numOfDeletedNotifications == 0) {
            throw new NoNotificationFoundException("Could not find notification with id: " + id);
        }
    }

    public void deleteByRecipientId(UUID recipientId) {
        notificationRepository.deleteByRecipientId(recipientId);
    }


    // event listeners

    @EventListener
    @Async
    public void handleUnacceptedProjectMembershipCreatedEvent(UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent) {
        String message = "You got invited to project " + unacceptedProjectMembershipCreatedEvent.getProjectId() + ".";

        Notification notification = new Notification(unacceptedProjectMembershipCreatedEvent.getInviteeId(), message);
        notificationRepository.save(notification);
    }

    @EventListener
    @Async
    public void handleTicketAssignedEvent(TicketAssignedEvent ticketAssignedEvent) {
        String message =
                "You got assigned to ticket " + ticketAssignedEvent.getTicketId() +
                " of project " + ticketAssignedEvent.getProjectId() + ".";

        Notification notification = new Notification(ticketAssignedEvent.getAssigneeId(), message);
        notificationRepository.save(notification);
    }

    @EventListener
    @Async
    public void handleTicketUnassignedEvent(TicketUnassignedEvent ticketUnassignedEvent) {
        String message =
                "Your assignment to ticket " + ticketUnassignedEvent.getTicketId() +
                " of project " + ticketUnassignedEvent.getProjectId() +
                " has been revoked" + ".";

        Notification notification = new Notification(ticketUnassignedEvent.getAssigneeId(), message);
        notificationRepository.save(notification);
    }

    @EventListener
    @Async
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        userDataOfNotificationRepository.save(new UserDataOfNotification(userCreatedEvent.getUserId(), userCreatedEvent.getEmailAddress()));
    }

    @EventListener
    @Async
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        UserDataOfNotification userDataOfNotification =
                userDataOfNotificationRepository
                        .findByUserId(userPatchedEvent.getUserId())
                        .get(0);
        userDataOfNotification.setUserEmail(userPatchedEvent.getEmailAddress());
        userDataOfNotificationRepository.save(userDataOfNotification);
    }

    @EventListener
    @Async
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        this.deleteByRecipientId(userDeletedEvent.getUserId());
        userDataOfNotificationRepository.deleteByUserId(userDeletedEvent.getUserId());
    }
}
