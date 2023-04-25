package com.kett.TicketSystem.ticket.domain;

import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import lombok.*;
import org.hibernate.annotations.Type;

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
    @Column(length = 16)
    private UUID projectId;

    @Getter
    @Setter
    @Column(length = 16)
    private UUID phaseId;

    @Getter
    @Type(type="uuid-char")
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> assigneeIds = new ArrayList<>();

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new TicketException("title must not be null or empty");
        }
        this.title = title;
    }

    public void setDueTime(LocalDateTime newDueTime) {
        if (newDueTime != null && newDueTime.isBefore(LocalDateTime.now())) {
            throw new TicketException("dueTime cannot be in the past");
        }
        this.dueTime = newDueTime;
    }

    protected void setProjectId(UUID projectId) {
        if (projectId == null) {
            throw new TicketException("projectId must not be null");
        }
        this.projectId = projectId;
    }

    public void setAssigneeIds(List<UUID> assigneeIds) {
        if (assigneeIds == null) {
            throw new TicketException("assigneeIds must not be null but it may be empty");
        }
        this.assigneeIds.clear();
        this.assigneeIds.addAll(assigneeIds);
    }

    public void removeAssignee(UUID userId) {
        assigneeIds.remove(userId);
    }

    public Boolean isAssignee(UUID assigneeId) {
        return this.assigneeIds.contains(assigneeId);
    }

    public Ticket(String title, String description, LocalDateTime dueTime, UUID projectId, UUID phaseId, List<UUID> assigneeIds) {
        this.setTitle(title);
        this.description = description;
        this.creationTime = LocalDateTime.now();
        this.setDueTime(dueTime);
        this.setPhaseId(phaseId);
        this.setProjectId(projectId);
        this.setAssigneeIds(assigneeIds);
    }
}
