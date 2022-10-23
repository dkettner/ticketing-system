package com.kett.TicketSystem.project.domain;

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
    private String cardStatus;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private UUID creatorId;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> assigneeIds = new ArrayList<>();


    public Ticket(String title, String description, LocalDateTime dueTime, UUID creatorId, List<UUID> assigneeIds) {
        if (title == null || title.isEmpty()) {
            throw new RuntimeException("title must not be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("description must not be null or empty");
        }
        if (dueTime == null) {
            throw new RuntimeException("dueTime must not be null");
        }
        if (dueTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("dueTime cannot be in the past");
        }
        if (creatorId == null) {
            throw new RuntimeException("creatorId must not be null");
        }
        if (assigneeIds == null) {
            throw new RuntimeException("memberIds must not be null");
        }

        this.title = title;
        this.description = title;
        this.creationTime = LocalDateTime.now();
        this.dueTime = dueTime;
        this.cardStatus = "TO_DO";
        this.creatorId = creatorId;
        this.assigneeIds.addAll(assigneeIds); // TODO: Check for duplicates?
        if (!this.assigneeIds.contains(creatorId)) {
            this.assigneeIds.add(creatorId);
        }
    }
}
