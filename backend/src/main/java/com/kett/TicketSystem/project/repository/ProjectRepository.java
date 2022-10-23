package com.kett.TicketSystem.project.repository;

import com.kett.TicketSystem.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Long removeById(UUID id);
}
