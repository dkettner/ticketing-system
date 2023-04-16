package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.ticket.domain.consumedData.MembershipDataOfTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipDataOfTicketRepository extends JpaRepository<MembershipDataOfTicket, UUID> {
    MembershipDataOfTicket findByMembershipId(UUID membershipId);
    Integer deleteByMembershipId(UUID membershipId);
    Integer deleteByUserId(UUID userId);
    List<MembershipDataOfTicket> findByProjectId(UUID projectId);
    Integer deleteByProjectId(UUID projectId);

    Boolean existsByUserId(UUID membershipId);
    boolean existsByUserIdAndProjectId(UUID assigneeId, UUID projectId);
    Boolean existsByMembershipIdAndProjectId(UUID membershipId, UUID projectId);
}
