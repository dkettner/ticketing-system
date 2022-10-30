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
    @Setter(AccessLevel.PROTECTED)
    private UUID projectId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Phase previousPhase;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Phase nextPhase;

    public Boolean isFirst() {
        return previousPhase == null;
    }

    public Boolean isLast() {
        return nextPhase == null;
    }

    public Phase(UUID projectId, String name, Phase previousPhase, Phase nextPhase) {
        if (projectId == null) {
            throw new PhaseException("projectId must not be null");
        }
        if (name == null) {
            throw new PhaseException("name must not be null");
        }

        this.projectId = projectId;
        this.name = name;
        this.previousPhase = previousPhase;
        this.nextPhase = nextPhase;
    }
}
