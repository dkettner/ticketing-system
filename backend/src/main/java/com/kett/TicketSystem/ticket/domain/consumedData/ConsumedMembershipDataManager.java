package com.kett.TicketSystem.ticket.domain.consumedData;

import com.kett.TicketSystem.common.IConsumedDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class ConsumedMembershipDataManager implements IConsumedDataManager<ProjectMembersVO> {
    private final List<ProjectMembersVO> projectMembersVOs = new ArrayList<>();

    @Override
    public Boolean add(ProjectMembersVO date) {
        if (this.exists(date.projectId())) {
            return false;
        }
        projectMembersVOs.add(date);
        return true;
    }

    @Override
    public Boolean overwrite(ProjectMembersVO date) {
        if (this.exists(date.projectId())) {
            this.remove(date.projectId());
            this.add(date);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean remove(UUID id) {
        return projectMembersVOs.removeIf(projectMembersVO -> projectMembersVO.projectId().equals(id));
    }

    public Boolean removeByPredicate(Predicate<ProjectMembersVO> predicate) {
        return projectMembersVOs.removeIf(predicate);
    }

    @Override
    public Optional<ProjectMembersVO> get(UUID id) {
        return projectMembersVOs
                .stream()
                .filter(projectMembersVO -> projectMembersVO.projectId().equals(id))
                .findFirst();
    }

    public List<ProjectMembersVO> getByPredicate(Predicate<ProjectMembersVO> predicate) {
        return projectMembersVOs
                .stream()
                .filter(predicate)
                .toList();
    }

    @Override
    public Boolean exists(UUID id) {
        return projectMembersVOs
                .stream()
                .anyMatch(projectMembersVO -> projectMembersVO.projectId().equals(id));
    }
}
