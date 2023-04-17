package com.kett.TicketSystem.notification.application;

import com.kett.TicketSystem.common.DtoMapper;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.notification.application.dto.NotificationPatchIsReadDto;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import com.kett.TicketSystem.notification.domain.Notification;
import com.kett.TicketSystem.notification.domain.NotificationDomainService;
import com.kett.TicketSystem.user.domain.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationApplicationService {
    private final NotificationDomainService notificationDomainService;
    private final UserDomainService userDomainService;
    private final DtoMapper dtoMapper;

    @Autowired
    public NotificationApplicationService(
            NotificationDomainService notificationDomainService,
            UserDomainService userDomainService, // TODO: remove UserDomainService Dependency
            DtoMapper dtoMapper
    ) {
        this.notificationDomainService = notificationDomainService;
        this.userDomainService = userDomainService;
        this.dtoMapper = dtoMapper;
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationDomainService.getGetRecipientIdByNotificationId(#id)))")
    public NotificationResponseDto getNotificationById(UUID id) {
        Notification notification = notificationDomainService.getNotificationById(id);
        return dtoMapper.mapNotificationToNotificationResponseDto(notification);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#recipientId))")
    public List<NotificationResponseDto> getNotificationsByRecipientId(UUID recipientId) {
        List<Notification> notifications = notificationDomainService.getNotificationsByRecipientId(recipientId);
        return dtoMapper.mapNotificationListToNotificationResponseDtoList(notifications);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@userDomainService.getUserIdByEmail(#email)))")
    public List<NotificationResponseDto> getNotificationsByEmail(EmailAddress email) {
        UUID recipientId = userDomainService.getUserIdByEmail(email);
        return this.getNotificationsByRecipientId(recipientId);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationDomainService.getGetRecipientIdByNotificationId(#id)))")
    public void patchNotificationReadState(UUID id, NotificationPatchIsReadDto notificationPatchIsReadDto) {
        notificationDomainService.patchReadState(id, notificationPatchIsReadDto.getIsRead());
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationDomainService.getGetRecipientIdByNotificationId(#id)))")
    public void deleteNotificationById(UUID id) {
        notificationDomainService.deleteById(id);
    }

}
