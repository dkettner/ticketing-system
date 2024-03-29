package com.kett.TicketSystem.phase.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseResponseDto {
    private UUID id;
    private UUID projectId;
    private String name;
    private UUID previousPhaseId;
    private UUID nextPhaseId;
    private Integer ticketCount;
}
