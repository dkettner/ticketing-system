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
    private LocalDateTime creationTime;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.LAZY)
    private List<UUID> ownerIds = new ArrayList<>();

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.LAZY)
    private List<UUID> memberIds = new ArrayList<>();

    public Project(String name, String description, UUID initialOwnerId, List<UUID> memberIds) {
        if (name == null || name.isEmpty()) {
            throw new ProjectException("name must not be null or empty");
        }
        if (initialOwnerId == null) {
            throw new ProjectException("initialOwnerId must not be null");
        }

        this.name = name;
        this.description = description;
        this.creationTime = LocalDateTime.now();
        this.ownerIds.add(initialOwnerId);
        if (memberIds != null) {
            this.memberIds.addAll(memberIds);
        }
        if (!this.memberIds.contains(initialOwnerId)) {
            this.memberIds.add(initialOwnerId);
        }
    }
}
