package com.kett.TicketSystem.user.domain;

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
    private String mailAddress;

    public User(String name, String mailAddress) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("name must not be null or empty");
        }
        if (mailAddress == null || mailAddress.isEmpty()) {
            throw new RuntimeException("mailAddress must not be null or empty");
        }

        this.name = name;
        this.mailAddress = mailAddress;
    }
}
