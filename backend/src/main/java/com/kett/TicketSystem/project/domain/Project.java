package com.kett.TicketSystem.project.domain;

import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private UUID creatorId;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> memberIds = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    public Project(String name, String description, UUID creatorId, List<UUID> memberIds) {
        if (name == null || name.isEmpty()) {
            throw new ProjectException("name must not be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new ProjectException("description must not be null or empty");
        }
        if (creatorId == null) {
            throw new ProjectException("creatorId must not be null");
        }
        if (memberIds == null) {
            throw new ProjectException("memberIds must not be null");
        }

        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.creationTime = LocalDateTime.now();
        this.memberIds.addAll(memberIds); // TODO: Check for duplicates?
        if (!this.memberIds.contains(creatorId)) {
            this.memberIds.add(creatorId);
        }
    }
}
