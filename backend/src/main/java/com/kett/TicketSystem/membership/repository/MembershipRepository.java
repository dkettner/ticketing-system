package com.kett.TicketSystem.membership.repository;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findByUserId(UUID userId);
    List<Membership> findByProjectId(UUID projectId);
    List<Membership> findByUserIdAndStateEquals(UUID projectId, State state);
    Long removeById(UUID id);

    Boolean existsByUserIdAndProjectId(UUID userId, UUID projectId);
}
