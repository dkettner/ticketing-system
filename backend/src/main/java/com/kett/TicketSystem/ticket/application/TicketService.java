package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket getTicketById(UUID id) {
        return ticketRepository
                .findById(id)
                .orElseThrow(() -> new NoTicketFoundException("could not find ticket with id: " + id));
    }
}
