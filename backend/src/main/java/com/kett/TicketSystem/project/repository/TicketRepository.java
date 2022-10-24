package com.kett.TicketSystem.project.repository;

import com.kett.TicketSystem.project.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketNumber(UUID ticketNumber);
    Long deleteByTicketNumber(UUID ticketNumber);
}
