package com.kett.TicketSystem.ticket.domain.consumedData;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDataOfTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    UUID id;

    @Column(length = 16)
    UUID userId;

    @Setter
    EmailAddress userEmail;

    public UserDataOfTicket(@NonNull UUID userId, @NonNull EmailAddress userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }
}
