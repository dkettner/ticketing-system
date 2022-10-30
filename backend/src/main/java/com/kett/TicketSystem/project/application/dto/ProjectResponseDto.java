package com.kett.TicketSystem.project.application.dto;

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
    private LocalDateTime creationTime;
    private List<UUID> ownerIds = new ArrayList<>();
    private List<UUID> memberIds = new ArrayList<>();
}
