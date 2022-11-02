package com.kett.TicketSystem.ticket.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketPatchDto {
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private UUID phaseId;
    private List<UUID> assigneeIds = new ArrayList<>();
}
