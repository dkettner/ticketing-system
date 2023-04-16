package com.kett.TicketSystem.ticket.domain.consumedData;

import java.util.List;
import java.util.UUID;

public record ProjectMembersVO(UUID projectId, List<UUID> memberIds) { }
