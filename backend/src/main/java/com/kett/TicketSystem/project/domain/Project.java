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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private UUID id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter(AccessLevel.PROTECTED)
    @Setter
    private UUID creatorId;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    @Getter
    @Setter
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> memberIds = new ArrayList<>();

    public Project(String name, String description, UUID creatorId, List<UUID> memberIds) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.memberIds.addAll(memberIds);
        this.creationTime = LocalDateTime.now();
    }
}
