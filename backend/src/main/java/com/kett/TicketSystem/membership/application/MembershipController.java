package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.common.exceptions.TooManyParametersException;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.dto.MembershipPutRoleDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutStateDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/memberships")
public class MembershipController {
    private final MembershipApplicationService membershipApplicationService;

    @Autowired
    public MembershipController(MembershipApplicationService membershipApplicationService) {
        this.membershipApplicationService = membershipApplicationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponseDto> getMembershipById(@PathVariable UUID id) {
        MembershipResponseDto membershipResponseDto = membershipApplicationService.getMembershipById(id);
        return new ResponseEntity<>(membershipResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponseDto>> getMembershipsByQuery(
            @RequestParam(name = "user-id", required = false) UUID userId,
            @RequestParam(name = "project-id", required = false) UUID projectId,
            @RequestParam(name = "email", required = false) String email
    ) {
        // TODO: What if another parameter gets added? This is too dirty.
        if (userId != null && projectId != null
            || userId != null && email != null
            || projectId != null && email != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<MembershipResponseDto> membershipResponseDtos;
        if (userId != null) {
            membershipResponseDtos = membershipApplicationService.getMembershipsByUserId(userId);
        } else if (projectId != null) {
            membershipResponseDtos = membershipApplicationService.getMembershipsByProjectId(projectId);
        } else if (email != null) {
            membershipResponseDtos = membershipApplicationService.getMembershipsByEmail(EmailAddress.fromString(email));
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }

        return new ResponseEntity<>(membershipResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MembershipResponseDto> postMembership(@RequestBody MembershipPostDto membershipPostDto) {
        MembershipResponseDto membershipResponseDto = membershipApplicationService.addMembership(membershipPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(membershipResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(membershipResponseDto);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<?> updateMembershipState(@PathVariable UUID id, @RequestBody MembershipPutStateDto membershipPutStateDto) {
        membershipApplicationService.updateMembershipState(id, membershipPutStateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateMembershipRole(@PathVariable UUID id, @RequestBody MembershipPutRoleDto membershipPutRoleDto) {
        membershipApplicationService.updateMembershipRole(id, membershipPutRoleDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMembership(@PathVariable UUID id) {
        membershipApplicationService.deleteMembershipById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
