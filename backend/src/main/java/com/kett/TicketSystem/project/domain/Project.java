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
    private String name;

    @Getter
    @Setter
    @Column(length = 1000)
    private String description;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new ProjectException("name must not be null or empty");
        }

        this.name = name;
    }

    public Project(String name, String description) {
        this.setName(name);
        this.description = description;
        this.creationTime = LocalDateTime.now();
    }
}
