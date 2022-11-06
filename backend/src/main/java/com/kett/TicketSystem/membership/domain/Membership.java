package com.kett.TicketSystem.membership.domain;

import com.kett.TicketSystem.membership.domain.exceptions.IllegalStateUpdateException;
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
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Enumerated(EnumType.STRING)
    private State state;

    public void setState(State state) {
        if (this.state.equals(state)) {
            throw new IllegalStateUpdateException("state of membership with id: " + this.id + " is already " + this.state);
        }

        if (this.state.equals(State.ACCEPTED) && state.equals(State.OPEN)) {
            throw new IllegalStateUpdateException(
                    "Once state as been changed to ACCEPTED, it cannot go back to OPEN. " +
                    "To revoke Membership, use delete."
            );
        }

        this.state = state;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new MembershipException("role cannot be null");
        }

        this.role = role;
    }

    public Membership(UUID projectId, UUID userId, Role role) {
        if (projectId == null) {
            throw new MembershipException("projectId cannot be null");
        }
        if (userId == null) {
            throw new MembershipException("userId cannot be null");
        }

        this.projectId = projectId;
        this.userId = userId;
        this.setRole(role);
        this.state = State.OPEN;
    }
}
