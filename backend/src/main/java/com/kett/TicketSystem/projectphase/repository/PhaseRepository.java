package com.kett.TicketSystem.projectphase.repository;

import com.kett.TicketSystem.projectphase.domain.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, UUID> {
}
