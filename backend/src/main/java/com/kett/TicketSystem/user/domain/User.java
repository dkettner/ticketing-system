package com.kett.TicketSystem.user.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
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
    private String name;

    @Getter
    @Column(unique = true)
    private EmailAddress email;

    @Getter
    private String password;

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new UserException("name must not be null or empty");
        }

        this.name = name;
    }

    public void setEmail(EmailAddress email) {
        if (email == null) {
            throw new UserException("email must not be null");
        }

        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new UserException("password must not be null or empty");
        }

        this.password = password;
    }

    public User(String name, EmailAddress email, String password) {
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
    }

    public User(String name, String email, String password) {
        this(name, EmailAddress.fromString(email), password);
    }
}
