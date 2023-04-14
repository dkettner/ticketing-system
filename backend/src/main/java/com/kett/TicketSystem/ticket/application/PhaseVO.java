package com.kett.TicketSystem.ticket.application;

import java.util.UUID;

public record PhaseVO(UUID id, UUID previousPhaseId, UUID projectId) { }
