package com.kett.TicketSystem.project.domain;

import com.kett.TicketSystem.project.domain.exceptions.TicketException;
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
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(unique = true)
    private UUID ticketNumber;

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
    @Setter
    private LocalDateTime dueTime;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private UUID creatorId;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> assigneeIds = new ArrayList<>();

    @OneToMany
    @Getter
    @Setter
    private List<Ticket> tickets = new ArrayList<>();

    public Ticket(String title, String description, LocalDateTime dueTime, UUID creatorId, List<UUID> assigneeIds) {
        if (title == null || title.isEmpty()) {
            throw new TicketException("title must not be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new TicketException("description must not be null or empty");
        }
        if (dueTime == null) {
            throw new TicketException("dueTime must not be null");
        }
        if (dueTime.isBefore(LocalDateTime.now())) {
            throw new TicketException("dueTime cannot be in the past");
        }
        if (creatorId == null) {
            throw new TicketException("creatorId must not be null");
        }
        if (assigneeIds == null) {
            throw new TicketException("memberIds must not be null");
        }

        this.ticketNumber = UUID.randomUUID();
        this.title = title;
        this.description = title;
        this.creationTime = LocalDateTime.now();
        this.dueTime = dueTime;
        this.ticketStatus = TicketStatus.TO_DO;
        this.creatorId = creatorId;
        this.assigneeIds.addAll(assigneeIds); // TODO: Check for duplicates?
        if (!this.assigneeIds.contains(creatorId)) {
            this.assigneeIds.add(creatorId);
        }
    }
}
