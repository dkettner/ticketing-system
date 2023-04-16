package com.kett.TicketSystem.membership.repository;

import com.kett.TicketSystem.membership.domain.consumedData.UserDataOfMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDataOfMembershipRepository extends JpaRepository<UserDataOfMembership, UUID> {
    UserDataOfMembership findByUserId(UUID userId);
    Integer deleteByUserId(UUID userId);

    Boolean existsByUserId(UUID userId);
}
