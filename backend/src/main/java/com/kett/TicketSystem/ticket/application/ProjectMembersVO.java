package com.kett.TicketSystem.ticket.application;

import java.util.List;
import java.util.UUID;

public record ProjectMembersVO(UUID projectId, List<UUID> memberIds) { }
