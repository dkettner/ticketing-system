
package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.common.DtoMapper;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutRoleDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutStateDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.MembershipDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipApplicationService {
    private final MembershipDomainService membershipDomainService;
    private final DtoMapper dtoMapper;

    @Autowired
    public MembershipApplicationService(MembershipDomainService membershipDomainService, DtoMapper dtoMapper) {
        this.membershipDomainService = membershipDomainService;
        this.dtoMapper = dtoMapper;
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@membershipDomainService.getProjectIdByMembershipId(#id))," +
            "'ROLE_USER_'.concat(@membershipDomainService.getUserIdByMembershipId(#id)))")
    public MembershipResponseDto getMembershipById(UUID id) {
        Membership membership = membershipDomainService.getMembershipById(id);
        return dtoMapper.mapMembershipToMembershipResponseDto(membership);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#userId))")
    public List<MembershipResponseDto> getMembershipsByUserId(UUID userId) {
        List<Membership> memberships = membershipDomainService.getMembershipsByUserId(userId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@membershipDomainService.getUserIdByUserEmailAddress(#email)))")
    public List<MembershipResponseDto> getMembershipsByEmail(EmailAddress email) {
        List<Membership> memberships = membershipDomainService.getMembershipsByUserEmail(email);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<MembershipResponseDto> getMembershipsByProjectId(UUID projectId) {
        List<Membership> memberships = membershipDomainService.getMembershipsByProjectId(projectId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#membershipPostDto.projectId))")
    public MembershipResponseDto addMembership(MembershipPostDto membershipPostDto) {
        Membership membership = membershipDomainService.addNewMembership(
                dtoMapper.mapMembershipPostDtoToMembership(membershipPostDto)
        );
        return dtoMapper.mapMembershipToMembershipResponseDto(membership);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@membershipDomainService.getUserIdByMembershipId(#id)))")
    public void updateMembershipState(UUID id, MembershipPutStateDto membershipPutStateDto) {
        membershipDomainService.updateMemberShipState(id, membershipPutStateDto.getState());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@membershipDomainService.getProjectIdByMembershipId(#id)))")
    public void updateMembershipRole(UUID id, MembershipPutRoleDto membershipPutRoleDto) {
        membershipDomainService.updateMembershipRole(id, membershipPutRoleDto.getRole());
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@membershipDomainService.getProjectIdByMembershipId(#id))," +
            "'ROLE_USER_'.concat(@membershipDomainService.getUserIdByMembershipId(#id)))")
    public void deleteMembershipById(UUID id) {
        membershipDomainService.deleteMembershipById(id);
    }
}
