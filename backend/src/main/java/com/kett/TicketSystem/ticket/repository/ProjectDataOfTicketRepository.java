package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.ticket.domain.consumedData.ProjectDataOfTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectDataOfTicketRepository extends JpaRepository<ProjectDataOfTicket, UUID> {
    ProjectDataOfTicket findByProjectId(UUID projectId);
    Integer deleteByProjectId(UUID projectId);

    Boolean existsByProjectId(UUID projectId);
}
