package com.kett.TicketSystem.project.repository;

import com.kett.TicketSystem.project.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends CrudRepository<Project, UUID> {
}
