package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByPhaseId(UUID phaseId);
    List<Ticket> findByProjectId(UUID projectId);
    List<Ticket> findByAssigneeIdsContaining(UUID assigneeId);
    List<Ticket> findByPhaseIdInAndAssigneeIdsContaining(List<UUID> phaseIds, UUID assigneeId);

    Boolean existsByPhaseIdEquals(UUID phaseId);

    void deleteByProjectId(UUID projectId);

    Long removeById(UUID id);
}
