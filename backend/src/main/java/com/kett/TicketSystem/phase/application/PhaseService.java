package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.LastPhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.application.exceptions.ImpossibleException;
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

    public Phase getPhaseById(UUID id) throws NoPhaseFoundException {
        return phaseRepository
                .findById(id)
                .orElseThrow(() -> new NoPhaseFoundException("could not find phase with id: " + id));
    }

    public UUID getProjectIdByPhaseId(UUID phaseId) throws NoPhaseFoundException {
        return this.getPhaseById(phaseId).getProjectId();
    }

    public List<Phase> getPhasesByProjectId(UUID projectId) throws NoPhaseFoundException {
        List<Phase> phases = phaseRepository.findByProjectId(projectId);
        if (phases.isEmpty()) {
            throw new NoPhaseFoundException("could not find phases with projectId: " + projectId);
        }
        return phases;
    }

    public Phase addPhase(Phase phase) throws UnrelatedPhaseException {
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

    public Optional<Phase> getFirstPhaseByProjectId(UUID projectId) {
        return phaseRepository.findByProjectIdAndPreviousPhaseIsNull(projectId);
    }

    private Phase addFirst(Phase phase) {
        Optional<Phase> nextPhase = getFirstPhaseByProjectId(phase.getProjectId());
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

    public void patchPhaseName(UUID id, String name) throws PhaseException, NoPhaseFoundException {
        Phase phase = this.getPhaseById(id);
        phase.setName(name);
        phaseRepository.save(phase);
    }

    public void deleteById(UUID id) throws NoPhaseFoundException, LastPhaseException {
        Phase phase = this.getPhaseById(id);

        if (phase.isFirst() && phase.isLast()) {
            throw new LastPhaseException(
                    "could not delete phase with id: " + id + " " +
                    "because it is the last phase of the project with id: " + phase.getProjectId()
            );
        }

        Phase previousPhase = phase.getPreviousPhase();
        Phase nextPhase = phase.getNextPhase();

        if (previousPhase != null) {
            previousPhase.setNextPhase(nextPhase);
            phaseRepository.save(previousPhase);
        }
        if (nextPhase != null) {
            nextPhase.setPreviousPhase(previousPhase);
            phaseRepository.save(nextPhase);
        }

        Long numOfDeletedPhases = phaseRepository.removeById(id);
        if (numOfDeletedPhases == 0) {
            throw new NoPhaseFoundException("could not delete because there was no phase with id: " + id);
        } else if (numOfDeletedPhases > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                            "Multiple phases were deleted when deleting phase with id: " + id
            );
        }
    }
}
