package com.kett.TicketSystem.user.domain;

import com.kett.TicketSystem.domainprimitives.EMailAddress;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @Column(unique = true)
    private EMailAddress eMailAddress;

    public User(String name, EMailAddress eMailAddress) {
        if (name == null || name.isEmpty()) {
            throw new UserException("name must not be null or empty");
        }
        if (eMailAddress == null) {
            throw new UserException("mailAddress must not be null or empty");
        }

        this.name = name;
        this.eMailAddress = eMailAddress;
    }

    public User(String name, String eMailAddress) {
        this(name, EMailAddress.fromString(eMailAddress));
    }
}
