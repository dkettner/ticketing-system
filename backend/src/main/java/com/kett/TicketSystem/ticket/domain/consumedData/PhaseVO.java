package com.kett.TicketSystem.ticket.domain.consumedData;

import java.util.UUID;

public record PhaseVO(UUID id, UUID previousPhaseId, UUID projectId) { }
