package com.kett.TicketSystem.project.application.dto;

import lombok.*;
import java.time.LocalDateTime;
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
}
