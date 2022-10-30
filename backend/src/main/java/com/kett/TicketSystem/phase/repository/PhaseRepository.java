package com.kett.TicketSystem.phase.repository;

import com.kett.TicketSystem.phase.domain.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, UUID> {
}
