package com.kett.TicketSystem.project.domain;

import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    public Project(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new ProjectException("name must not be null or empty");
        }

        this.name = name;
        this.description = description;
        this.creationTime = LocalDateTime.now();
    }
}
