package com.kett.TicketSystem.membership.domain;

import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
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
            throw new MembershipException("projectId cannot be null");
        }
        if (userId == null) {
            throw new MembershipException("userId cannot be null");
        }
        if (role == null) {
            throw new MembershipException("role cannot be null");
        }

        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
        this.state = State.OPEN;
    }
}
