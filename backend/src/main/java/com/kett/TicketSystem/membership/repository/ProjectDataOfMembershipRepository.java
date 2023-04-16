package com.kett.TicketSystem.membership.repository;

import com.kett.TicketSystem.membership.domain.consumedData.ProjectDataOfMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectDataOfMembershipRepository extends JpaRepository<ProjectDataOfMembership, UUID> {
    ProjectDataOfMembership findByProjectId(UUID projectId);
    Integer deleteByProjectId(UUID projectId);

    Boolean existsByProjectId(UUID projectId);
}
