package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.ticket.domain.consumedData.UserDataOfTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDataOfTicketRepository extends JpaRepository<UserDataOfTicket, UUID> {
    List<UserDataOfTicket> findByUserId(UUID userId);
    List<UserDataOfTicket> findByUserEmailEquals(EmailAddress emailAddress);
    Integer deleteByUserId(UUID userId);
}
