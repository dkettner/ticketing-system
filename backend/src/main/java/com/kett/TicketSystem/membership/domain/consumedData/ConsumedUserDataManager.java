package com.kett.TicketSystem.membership.domain.consumedData;

import com.kett.TicketSystem.common.IConsumedDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsumedUserDataManager implements IConsumedDataManager<UUID> {
    // TODO: use persistent storage instead
    List<UUID> existingUsers = new ArrayList<>(); // MembershipDomainService only cares about the IDs of the users

    @Override
    public Boolean add(UUID date) {
        if (this.exists(date)) {
            return false;
        }

        existingUsers.add(date);
        return true;
    }

    @Override
    public Boolean overwrite(UUID date) {
        if (this.exists(date)) {
            existingUsers.remove(date);
            existingUsers.add(date);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean remove(UUID id) {
        return existingUsers.remove(id);
    }

    @Override
    public Optional<UUID> get(UUID id) {  // in this case redundant
        return existingUsers
                .stream()
                .filter(projectId -> projectId.equals(id))
                .findFirst();
    }

    @Override
    public Boolean exists(UUID id) {
        return existingUsers.contains(id);
    }
}
