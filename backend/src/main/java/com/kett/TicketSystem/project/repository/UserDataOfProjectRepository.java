package com.kett.TicketSystem.project.repository;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.project.domain.consumedData.UserDataOfProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDataOfProjectRepository extends JpaRepository<UserDataOfProject, UUID> {
    List<UserDataOfProject> findByUserId(UUID userId);
    List<UserDataOfProject> findByUserEmailEquals(EmailAddress emailAddress);

    Integer deleteByUserId(UUID userId);
}
