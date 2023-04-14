package com.kett.TicketSystem.phase.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import com.kett.TicketSystem.phase.domain.Phase;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PhasePositionUpdatedEvent extends DomainEvent {
    private final UUID phaseId;
    private final UUID previousPhaseId;
    private final UUID projectId;

    public PhasePositionUpdatedEvent(UUID phaseId, Phase previousPhase, UUID projectId) {
        super();
        this.phaseId = phaseId;
        this.previousPhaseId = previousPhase == null ? null: previousPhase.getId();
        this.projectId = projectId;
    }

    public PhasePositionUpdatedEvent(UUID phaseId, UUID previousPhaseId, UUID projectId) {
        super();
        this.phaseId = phaseId;
        this.previousPhaseId = previousPhaseId;
        this.projectId = projectId;
    }
}
