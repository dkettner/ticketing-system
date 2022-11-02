package com.kett.TicketSystem.ticket.domain;

import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    @Getter
    private LocalDateTime dueTime;

    @Getter
    @Setter
    @Column(length = 16)
    private UUID phaseId;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> assigneeIds = new ArrayList<>();

    public void setDueTime(LocalDateTime newDueTime) {
        if (newDueTime != null && newDueTime.isBefore(LocalDateTime.now())) {
            throw new TicketException("dueTime cannot be in the past");
        }
        this.dueTime = newDueTime;
    }

    public Ticket(String title, String description, LocalDateTime dueTime, List<UUID> assigneeIds) {
        if (title == null || title.isEmpty()) {
            throw new TicketException("title must not be null or empty");
        }
        if (assigneeIds == null) {
            throw new TicketException("memberIds must not be null");
        }

        this.title = title;
        this.description = description;
        this.creationTime = LocalDateTime.now();
        this.setDueTime(dueTime);
        this.assigneeIds.addAll(assigneeIds);
    }
}
