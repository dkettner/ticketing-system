package com.kett.TicketSystem.membership.domain.consumedData;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDataOfMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    UUID id;

    @Column(length = 16, unique = true)
    UUID userId;

    @Setter
    @Column(unique = true)
    EmailAddress userEmail;

    public UserDataOfMembership(@NonNull UUID userId, @NonNull EmailAddress userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }
}
