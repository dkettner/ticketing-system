package com.kett.TicketSystem.membership.application.dto;

import com.kett.TicketSystem.membership.domain.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembershipPutStateDto {
    private State state;
}
