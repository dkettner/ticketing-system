package com.kett.TicketSystem.phase.repository;

import com.kett.TicketSystem.phase.domain.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, UUID> {
    List<Phase> findByProjectId(UUID projectId);
    Optional<Phase> findByProjectIdAndPreviousPhaseIsNull(UUID projectId);

    Long removeById(UUID id);
    List<Phase> deleteByProjectId(UUID projectId);
}
