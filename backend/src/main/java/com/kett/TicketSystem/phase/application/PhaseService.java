package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.phase.domain.events.PhasePositionUpdatedEvent;
import com.kett.TicketSystem.phase.domain.exceptions.LastPhaseException;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.common.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.exceptions.PhaseIsNotEmptyException;
import com.kett.TicketSystem.ticket.domain.events.TicketCreatedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketDeletedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketPhaseUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PhaseService {
    private final PhaseRepository phaseRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ConsumedProjectDataManager consumedProjectDataManager;

    @Autowired
    public PhaseService(PhaseRepository phaseRepository, ApplicationEventPublisher eventPublisher) {
        this.phaseRepository = phaseRepository;
        this.eventPublisher = eventPublisher;
        this.consumedProjectDataManager = new ConsumedProjectDataManager();
    }


    // create

    public Phase addPhase(Phase phase, UUID previousPhaseId) throws NoPhaseFoundException, UnrelatedPhaseException {
        if (!consumedProjectDataManager.exists(phase.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + phase.getProjectId());
        }

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

        Phase initializedPhase;
        if (previousPhase == null) {
            initializedPhase = addFirst(phase);
        } else {
            initializedPhase = addAfterPrevious(phase, previousPhase);
        }
        eventPublisher.publishEvent(
                new PhaseCreatedEvent(
                        initializedPhase.getId(),
                        initializedPhase.getPreviousPhase(),
                        initializedPhase.getProjectId()
                )
        );
        return initializedPhase;
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
        Phase patchedPhase = this.getPhaseById(id);
        Phase oldNextPhase = patchedPhase.getNextPhase();
        Phase oldPreviousPhase = patchedPhase.getPreviousPhase();

        this.removePhaseFromCurrentPosition(patchedPhase);
        this.addPhase(patchedPhase, previousPhaseId);

        // up to three positions updated -> up to three events published
        List<PhasePositionUpdatedEvent> events = new ArrayList<>();
        events.add(new PhasePositionUpdatedEvent(patchedPhase.getId(), previousPhaseId, patchedPhase.getProjectId()));
        if (oldNextPhase != null) {
            events.add(new PhasePositionUpdatedEvent(oldNextPhase.getId(), oldPreviousPhase, patchedPhase.getProjectId()));
        }
        if (previousPhaseId != null) {
            events.add(new PhasePositionUpdatedEvent(previousPhaseId, patchedPhase.getId(), patchedPhase.getProjectId()));
        }
        events.forEach(eventPublisher::publishEvent);
    }


    // delete

    public void deleteById(UUID id) throws NoPhaseFoundException, LastPhaseException {
        Phase phase = this.getPhaseById(id);
        if (phase.isFirst() && phase.isLast()) {
            throw new LastPhaseException(
                    "The phase with id: " + phase.getId() +
                    " is already the last phase of the project with id: " + phase.getProjectId() +
                    " and cannot be deleted."
            );
        }
        if (phase.getTicketCount() != 0) {
            throw new PhaseIsNotEmptyException("phase with id: \" + id + \" is not empty and can not be deleted");
        }

        this.removePhaseFromCurrentPosition(phase);
        phaseRepository.removeById(id);
        eventPublisher.publishEvent(new PhaseDeletedEvent(phase.getId(), phase.getProjectId()));
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
        List<Phase> deletedPhases = phaseRepository.deleteByProjectId(projectId); // bypasses last phase check
        deletedPhases.forEach( phase ->
                eventPublisher.publishEvent(new PhaseDeletedEvent(phase.getId(), phase.getProjectId()))
        );
    }


    // event listeners

    @EventListener
    @Async
    public void handleDefaultProjectCreated(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        this.consumedProjectDataManager.add(defaultProjectCreatedEvent.getProjectId());

        Phase backlog = new Phase(defaultProjectCreatedEvent.getProjectId(), "BACKLOG", null, null);
        Phase doing = new Phase(defaultProjectCreatedEvent.getProjectId(), "DOING", null, null);
        Phase review = new Phase(defaultProjectCreatedEvent.getProjectId(), "REVIEW", null, null);
        Phase done = new Phase(defaultProjectCreatedEvent.getProjectId(), "DONE", null, null);

        this.addPhase(done, null);
        this.addPhase(review, null);
        this.addPhase(doing, null);
        this.addPhase(backlog, null);
    }

    @EventListener
    @Async
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        this.consumedProjectDataManager.remove(projectDeletedEvent.getProjectId());
        this.deletePhasesByProjectId(projectDeletedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        this.consumedProjectDataManager.add(projectCreatedEvent.getProjectId());
        this.addPhase(
                new Phase(projectCreatedEvent.getProjectId(), "BACKLOG", null, null),
                null
        );
    }

    @EventListener
    public void handleTicketCreatedEvent(TicketCreatedEvent ticketCreatedEvent) {
        Phase firstPhaseOfProject =
                getFirstPhaseByProjectId(ticketCreatedEvent.getProjectId())
                .orElseThrow( () ->
                    new ImpossibleException("The project with id: " + ticketCreatedEvent.getProjectId() + " has no phases.")
                );

        firstPhaseOfProject.increaseTicketCount();
        phaseRepository.save(firstPhaseOfProject);
    }

    @EventListener
    public void handleTicketPhaseUpdatedEvent(TicketPhaseUpdatedEvent ticketPhaseUpdatedEvent) {
        Phase oldPhase = this.getPhaseById(ticketPhaseUpdatedEvent.getOldPhaseId());
        Phase newPhase = this.getPhaseById(ticketPhaseUpdatedEvent.getNewPhaseId());

        // TODO: publish event to initiate rollback ?
        if (!oldPhase.getProjectId().equals(newPhase.getProjectId())) {
            throw new UnrelatedPhaseException(
                    "The update of ticket: " + ticketPhaseUpdatedEvent.getTicketId() + " caused a conflict: " +
                    "the old phase (id: " + oldPhase.getId() + ", projectId: " + oldPhase.getProjectId() + ") " +
                    "and new phase (id: " + newPhase.getId() + ", projectId: " + newPhase.getProjectId() + ") " +
                    "are not related."
            );
        }

        oldPhase.decreaseTicketCount();
        newPhase.increaseTicketCount();

        phaseRepository.save(oldPhase);
        phaseRepository.save(newPhase);
    }

    @EventListener
    public void handleTicketDeletedEvent(TicketDeletedEvent ticketDeletedEvent) {
        Phase phase = this.getPhaseById(ticketDeletedEvent.getPhaseId());
        phase.decreaseTicketCount();
        phaseRepository.save(phase);
    }
}
