package com.kett.TicketSystem.phase.domain;

import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Column(length = 16)
    private UUID projectId;

    @Getter
    private String name;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Phase previousPhase;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Phase nextPhase;

    protected void setProjectId(UUID projectId) {
        if (projectId == null) {
            throw new PhaseException("projectId must not be null");
        }
        this.projectId = projectId;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new PhaseException("name must not be null or empty");
        }
        this.name = name;
    }

    public Boolean isFirst() {
        return previousPhase == null;
    }

    public Boolean isLast() {
        return nextPhase == null;
    }

    public Phase(UUID projectId, String name, Phase previousPhase, Phase nextPhase) {
        this.setProjectId(projectId);
        this.setName(name);
        this.setPreviousPhase(previousPhase);
        this.setNextPhase(nextPhase);
    }
}
