package com.kett.TicketSystem.project.application.dto;

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
public class TicketPostDto {
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private UUID creatorId;
    private List<UUID> assigneeIds = new ArrayList<>();
}
