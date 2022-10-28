package com.kett.TicketSystem.project.application.dto;

import com.kett.TicketSystem.project.domain.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {
    private UUID ticketNumber;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime dueTime;
    private TicketStatus ticketStatus;
    private UUID creatorId;
    private List<UUID> assigneeIds = new ArrayList<>();
}
