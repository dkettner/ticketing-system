package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhaseService {
    private final PhaseRepository phaseRepository;

    @Autowired
    public PhaseService(PhaseRepository phaseRepository) {
        this.phaseRepository = phaseRepository;
    }

    public Phase getPhaseById(UUID id) {
        return phaseRepository
                .findById(id)
                .orElseThrow(() -> new NoPhaseFoundException("could not find phase with id: " + id));
    }

    public List<Phase> getPhasesByProjectId(UUID projectId) {
        List<Phase> phases = phaseRepository.findByProjectId(projectId);
        if (phases.isEmpty()) {
            throw new NoPhaseFoundException("could not find phases with projectId: " + projectId);
        }
        return phases;
    }

    public Phase addPhase(Phase phase) {
        UUID projectId = phase.getProjectId();
        Phase previousPhase = phase.getPreviousPhase();
        if (previousPhase != null && !previousPhase.getProjectId().equals(projectId)) {
            throw new UnrelatedPhaseException(
                    "previous phase with id: " + previousPhase.getId() +
                    " does not belong to project with id: " + projectId
            );
        }

        if (previousPhase == null) {
            return addFirst(phase);
        } else {
            return addAfterPrevious(phase, previousPhase);
        }
    }

    private Phase addFirst(Phase phase) {
        Optional<Phase> nextPhase = phaseRepository.findByProjectIdAndPreviousPhaseIsNull(phase.getProjectId());
        if (nextPhase.isPresent()) {
            phase.setNextPhase(nextPhase.get());
            nextPhase.get().setPreviousPhase(phase);

            phaseRepository.save(nextPhase.get());
        }

        return phaseRepository.save(phase);
    }

    private Phase addAfterPrevious(Phase phase, Phase previousPhase) {
        phase.setPreviousPhase(previousPhase); // may be redundant

        Phase nextPhase = previousPhase.getNextPhase();
        if (nextPhase != null) {
            phase.setNextPhase(nextPhase);
            nextPhase.setPreviousPhase(phase);
            phaseRepository.save(nextPhase);
        } else {
            phase.setNextPhase(null);
        }

        previousPhase.setNextPhase(phase);
        phaseRepository.save(previousPhase);

        return phaseRepository.save(phase);
    }
}
