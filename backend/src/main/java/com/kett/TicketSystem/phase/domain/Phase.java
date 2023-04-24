package com.kett.TicketSystem.phase.domain;

import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.common.exceptions.UnrelatedPhaseException;
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
    @OneToOne(fetch = FetchType.LAZY)
    private Phase previousPhase;

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    private Phase nextPhase;

    @Getter
    private Integer ticketCount;

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

    public void setPreviousPhase(Phase phase) {
        if (phase != null && !this.projectId.equals(phase.getProjectId())) {
            throw new UnrelatedPhaseException(
                    "The new previousPhase with id: " + phase.getId() + " belongs to the project with id: " + phase.getProjectId() +
                    " but the phase with id: " + this.id + " does not. It belongs to the project with id: " + this.projectId + "."
            );
        }
        this.previousPhase = phase;
    }

    public void setNextPhase(Phase phase) {
        if (phase != null && !this.projectId.equals(phase.getProjectId())) {
            throw new UnrelatedPhaseException(
                    "The new nextPhase with id: " + phase.getId() + " belongs to the project with id: " + phase.getProjectId() +
                    " but the phase with id: " + this.id + " does not. It belongs to the project with id: " + this.projectId + "."
            );
        }
        this.nextPhase = phase;
    }

    public void setTicketCount(int ticketCount) throws PhaseException {
        if (ticketCount < 0) {
            throw new PhaseException("ticketCount cannot be negative");
        }
        this.ticketCount = ticketCount;
    }

    public void increaseTicketCount() throws PhaseException {
        this.setTicketCount(
                this.getTicketCount() + 1
        );
    }

    public void decreaseTicketCount() throws PhaseException {
        this.setTicketCount(
                this.getTicketCount() - 1
        );
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
        this.setTicketCount(0);
    }
}
