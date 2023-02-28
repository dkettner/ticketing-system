package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.common.IConsumedDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsumedProjectDataManager implements IConsumedDataManager<UUID> {
    // TODO: use persistent storage instead
    List<UUID> existingProjects = new ArrayList<>(); // PhaseService only cares about the IDs of the projects

    @Override
    public Boolean add(UUID date) {
        if (this.exists(date)) {
            return false;
        }

        existingProjects.add(date);
        return true;
    }

    @Override
    public Boolean overwrite(UUID date) {
        if (this.exists(date)) {
            existingProjects.remove(date);
            existingProjects.add(date);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean remove(UUID id) {
        return existingProjects.remove(id);
    }

    @Override
    public Optional<UUID> get(UUID id) {  // in this case redundant
        return existingProjects
                .stream()
                .filter(projectId -> projectId.equals(id))
                .findFirst();
    }

    @Override
    public Boolean exists(UUID id) {
        return existingProjects.contains(id);
    }
}
