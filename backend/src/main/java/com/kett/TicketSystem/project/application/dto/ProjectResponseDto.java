package com.kett.TicketSystem.project.application.dto;

import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {
    private UUID id;
    private String name;
    private String description;
    private UUID creatorId;
    private LocalDateTime creationTime;
    private List<UUID> memberIds = new ArrayList<>();
    private List<TicketResponseDto> tickets = new ArrayList<>();
}
