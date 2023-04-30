package com.kett.TicketSystem.ticket.domain.consumedData;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDataOfTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    UUID id;

    @Column(length = 16)
    UUID projectId;

    public ProjectDataOfTicket(@NonNull UUID projectId) {
        this.projectId = projectId;
    }
}
