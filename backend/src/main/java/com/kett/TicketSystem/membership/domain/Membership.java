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
    private UUID UserId;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    private String state;
}
