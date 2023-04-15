package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.common.IConsumedDataManager;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class ConsumedPhaseDataManager implements IConsumedDataManager<PhaseVO> {
    private final List<PhaseVO> phases = new ArrayList<>();

    @Override
    public Boolean add(PhaseVO date) {
        if (this.exists(date.id())) {
            // if it already exists, then date is stale data
            return false;
        }

        phases.add(date);
        return true;
    }

    @Override
    public Boolean overwrite(PhaseVO date) {
        if (this.exists(date.id())) {
            this.remove(date.id());
        }
        phases.add(date);
        return true;
    }

    @Override
    public Boolean remove(UUID id) {
        return phases.removeIf(phaseVO -> phaseVO.id().equals(id));
    }

    public Boolean removeByPredicate(Predicate<PhaseVO> predicate) {
        return phases.removeIf(predicate);
    }

    public PhaseVO getFirstPhaseByProjectId(UUID projectId) {
        return phases
                .stream()
                .filter(phaseVO ->
                        phaseVO.projectId().equals(projectId)
                        && phaseVO.previousPhaseId() == null)
                .findFirst()
                .orElseThrow(() -> new ImpossibleException(
                        "could not find phaseVO that matches criteria to be first phase of project: " + projectId
                ));
    }

    @Override
    public Optional<PhaseVO> get(UUID id) {
        return phases
                .stream()
                .filter(phaseVO -> phaseVO.id().equals(id))
                .findFirst();
    }

    @Override
    public Boolean exists(UUID id) {
        return phases
                .stream()
                .anyMatch(phaseVO -> phaseVO.id().equals(id));
    }
}
