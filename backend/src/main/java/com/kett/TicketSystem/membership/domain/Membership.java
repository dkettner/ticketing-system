package com.kett.TicketSystem.membership.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID projectId;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID userId;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private State state;


    public Membership(UUID projectId, UUID userId, Role role) {
        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }
        if (userId == null) {
            throw new RuntimeException("userId cannot be null");
        }
        if (role == null) {
            throw new RuntimeException("role cannot be null");
        }

        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
        this.state = State.OPEN;
    }
}
