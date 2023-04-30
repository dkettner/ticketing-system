package com.kett.TicketSystem.ticket.domain.consumedData;


import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseDataOfTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    UUID id;

    @Column(length = 16)
    UUID phaseId;

    @Setter
    @Column(length = 16)
    UUID previousPhaseId;

    @Column(length = 16)
    UUID projectId;

    public PhaseDataOfTicket(@NonNull UUID phaseId, UUID previousPhaseId, @NonNull UUID projectId) {
        this.phaseId = phaseId;
        this.previousPhaseId = previousPhaseId;
        this.projectId = projectId;
    }
}
