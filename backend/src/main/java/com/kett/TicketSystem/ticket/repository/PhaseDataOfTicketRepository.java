package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.ticket.domain.consumedData.PhaseDataOfTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhaseDataOfTicketRepository extends JpaRepository<PhaseDataOfTicket, UUID> {
    List<PhaseDataOfTicket> findByPhaseId(UUID phaseId);
    Integer deleteByPhaseId(UUID projectId);
    List<PhaseDataOfTicket> findByProjectId(UUID projectId);
    List<PhaseDataOfTicket> findByProjectIdAndPreviousPhaseIdIsNull(UUID projectId);
    Integer deleteByProjectId(UUID projectId);

    Boolean existsByPhaseId(UUID phaseId);
    Boolean existsByPhaseIdAndProjectId(UUID phaseId, UUID projectId);
}
