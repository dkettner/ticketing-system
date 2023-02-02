package com.kett.TicketSystem.notification.domain;

import com.kett.TicketSystem.common.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.notification.domain.exceptions.NotificationException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID recipientId;

    @Getter
    private Boolean isRead;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 1000)
    private String content; // TODO: exchange for different types of events (invitation, ticketAssigned etc.)

    public void setIsRead(Boolean isReadStatus) {
        if (isReadStatus == null) {
            throw new NotificationException("isRead must not be null");
        }
        if (this.isRead) {
            throw new IllegalStateUpdateException(
                    "The notification with id: " + this.id + " has already been read " +
                    "but you provided \"" + isReadStatus + "\" ");
        }

        this.isRead = isReadStatus;
    }

    public Notification(UUID recipientId, String content) {
        this.creationTime = LocalDateTime.now();
        this.recipientId = recipientId;
        this.isRead = Boolean.FALSE;
        this.content = content;
    }
}
