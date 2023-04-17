package com.kett.TicketSystem.membership.repository;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.domain.consumedData.UserDataOfMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDataOfMembershipRepository extends JpaRepository<UserDataOfMembership, UUID> {
    List<UserDataOfMembership> findByUserId(UUID userId);
    List<UserDataOfMembership> findByUserEmailEquals(EmailAddress emailAddress);
    Integer deleteByUserId(UUID userId);

    Boolean existsByUserId(UUID userId);
}
