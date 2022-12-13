package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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


    // create

    public Phase addPhase(Phase phase, UUID previousPhaseId) throws NoPhaseFoundException, UnrelatedPhaseException {
        Phase previousPhase = null;
        if (previousPhaseId != null) {
            previousPhase = this.getPhaseById(previousPhaseId);
        }

        UUID projectId = phase.getProjectId();
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
        Optional<Phase> nextPhase = getFirstPhaseByProjectId(phase.getProjectId());

        phaseRepository.save(phase);    // make sure that phase has an id, may be redundant

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


    // read

    public Phase getPhaseById(UUID id) throws NoPhaseFoundException {
        return phaseRepository
                .findById(id)
                .orElseThrow(() -> new NoPhaseFoundException("could not find phase with id: " + id));
    }

    public List<Phase> getPhasesByProjectId(UUID projectId) throws NoPhaseFoundException {
        List<Phase> phases = phaseRepository.findByProjectId(projectId);
        if (phases.isEmpty()) {
            throw new NoPhaseFoundException("could not find phases with projectId: " + projectId);
        }
        return phases;
    }

    public Optional<Phase> getFirstPhaseByProjectId(UUID projectId) {
        return phaseRepository.findByProjectIdAndPreviousPhaseIsNull(projectId);
    }

    public UUID getProjectIdByPhaseId(UUID phaseId) throws NoPhaseFoundException {
        return this.getPhaseById(phaseId).getProjectId();
    }


    // update

    public void patchPhaseName(UUID id, String name) throws PhaseException, NoPhaseFoundException {
        Phase phase = this.getPhaseById(id);
        phase.setName(name);
        phaseRepository.save(phase);
    }

    public void patchPhasePosition(UUID id, UUID previousPhaseId) throws PhaseException, NoPhaseFoundException {
        Phase phase = this.getPhaseById(id);
        this.removePhaseFromCurrentPosition(phase);
        this.addPhase(phase, previousPhaseId);
    }


    // delete

    public void deleteById(UUID id) throws NoPhaseFoundException {
        Phase phase = this.getPhaseById(id);

        this.removePhaseFromCurrentPosition(phase);

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

    private void removePhaseFromCurrentPosition(Phase phase) {
        Phase previousPhase = phase.getPreviousPhase();
        Phase nextPhase = phase.getNextPhase();

        if (previousPhase != null) {
            previousPhase.setNextPhase(nextPhase);
            phaseRepository.save(previousPhase);
            phase.setPreviousPhase(null);
        }
        if (nextPhase != null) {
            nextPhase.setPreviousPhase(previousPhase);
            phaseRepository.save(nextPhase);
            phase.setNextPhase(null);
        }

        phaseRepository.save(phase);
    }

    public void deletePhasesByProjectId(UUID projectId) {
        phaseRepository.deleteByProjectId(projectId);
    }


    // event listeners

    @EventListener
    @Async
    public void handleDefaultProjectCreated(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        Phase toDo = new Phase(defaultProjectCreatedEvent.getProjectId(), "TO DO", null, null);
        Phase doing = new Phase(defaultProjectCreatedEvent.getProjectId(), "DOING", null, null);
        Phase review = new Phase(defaultProjectCreatedEvent.getProjectId(), "REVIEW", null, null);
        Phase done = new Phase(defaultProjectCreatedEvent.getProjectId(), "DONE", null, null);

        this.addPhase(done, null);
        this.addPhase(review, null);
        this.addPhase(doing, null);
        this.addPhase(toDo, null);
    }

    @EventListener
    @Async
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        this.deletePhasesByProjectId(projectDeletedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        this.addPhase(
                new Phase(projectCreatedEvent.getProjectId(), "Backlog", null, null),
                null
        );
    }

    public Boolean hasPhasesWithProjectId(UUID projectId) {
        return phaseRepository
                .findByProjectId(projectId)
                .size() > 0;
    }
}
