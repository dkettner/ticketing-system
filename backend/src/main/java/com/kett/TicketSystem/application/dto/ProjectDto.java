package com.kett.TicketSystem.application.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private UUID id;
    private String name;
    private String description;
    private UUID creatorId;
    private LocalDateTime creationTime;
    private List<UUID> memberIds = new ArrayList<>();
}
