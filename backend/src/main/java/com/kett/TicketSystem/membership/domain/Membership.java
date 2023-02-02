package com.kett.TicketSystem.membership.domain;

import com.kett.TicketSystem.common.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    @Column(length = 16)
    private UUID projectId;

    @Getter
    @Column(length = 16)
    private UUID userId;

    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Enumerated(EnumType.STRING)
    private State state;

    public Boolean isAccepted() {
        return this.state.equals(State.ACCEPTED);
    }

    protected void setProjectId(UUID projectId) {
        if (projectId == null) {
            throw new MembershipException("projectId cannot be null");
        }
        this.projectId = projectId;
    }

    protected void setUserId(UUID userId) {
        if (userId == null) {
            throw new MembershipException("userId cannot be null");
        }
        this.userId = userId;
    }

    public void setState(State state) {
        if (state == null) {
            throw new MembershipException("state must not be null");
        }

        if (this.state != null) {
            if (this.state.equals(state)) {
                throw new IllegalStateUpdateException("state of membership with id: " + this.id + " is already " + this.state);
            }

            if (this.state.equals(State.ACCEPTED) && state.equals(State.OPEN)) {
                throw new IllegalStateUpdateException(
                        "Once state has been changed to ACCEPTED, it cannot go back to OPEN. " +
                        "To revoke Membership, use delete."
                );
            }
        }


        this.state = state;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new MembershipException("role cannot be null");
        }
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return  "ROLE_" +
                "PROJECT_" +
                this.role.toString() + "_" +
                this.projectId.toString();
    }

    public Membership(UUID projectId, UUID userId, Role role) {
        this.setProjectId(projectId);
        this.setUserId(userId);
        this.setRole(role);
        this.setState(State.OPEN);
    }
}
