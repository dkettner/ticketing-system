package com.kett.TicketSystem.ticket.domain.consumedData;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipDataOfTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    UUID id;

    @Column(length = 16)
    UUID membershipId;

    @Column(length = 16)
    UUID userId;

    @Column(length = 16)
    UUID projectId;

    public MembershipDataOfTicket(@NonNull UUID membershipId, @NonNull UUID userId, @NonNull UUID projectId) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.projectId = projectId;
    }
}
