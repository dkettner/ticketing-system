package com.kett.TicketSystem.project.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPostDto {
    private String name;
    private String description;
    private UUID initialOwnerId;
    private List<UUID> memberIds = new ArrayList<>();
}
