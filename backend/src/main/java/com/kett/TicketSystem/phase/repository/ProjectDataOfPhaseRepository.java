package com.kett.TicketSystem.phase.repository;

import com.kett.TicketSystem.phase.domain.consumedData.ProjectDataOfPhase;
import com.kett.TicketSystem.ticket.domain.consumedData.ProjectDataOfTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectDataOfPhaseRepository extends JpaRepository<ProjectDataOfPhase, UUID> {
    ProjectDataOfTicket findByProjectId(UUID projectId);
    Integer deleteByProjectId(UUID projectId);

    Boolean existsByProjectId(UUID projectId);
}
