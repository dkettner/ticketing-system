package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.common.exceptions.TooManyParametersException;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.dto.MembershipPatchRoleDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPatchStateDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import org.slf4j.MDC;
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
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/memberships")
public class MembershipController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public MembershipController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponseDto> getMembershipById(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        MembershipResponseDto membershipResponseDto = ticketSystemService.getMembershipById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(membershipResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponseDto>> getMembershipsByQuery(
            @RequestParam(name = "user-id", required = false) UUID userId,
            @RequestParam(name = "project-id", required = false) UUID projectId,
            @RequestParam(name = "email", required = false) String email
    ) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        // TODO: What if another parameter gets added? This is too dirty.
        if (userId != null && projectId != null
            || userId != null && email != null
            || projectId != null && email != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<MembershipResponseDto> membershipResponseDtos;
        if (userId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByUserId(userId);
        } else if (projectId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByProjectId(projectId);
        } else if (email != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByEmail(EmailAddress.fromString(email));
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }

        MDC.remove("transactionId");
        return new ResponseEntity<>(membershipResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MembershipResponseDto> postMembership(@RequestBody MembershipPostDto membershipPostDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        MembershipResponseDto membershipResponseDto = ticketSystemService.addMembership(membershipPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(membershipResponseDto.getId())
                .toUri();

        MDC.remove("transactionId");
        return ResponseEntity
                .created(returnURI)
                .body(membershipResponseDto);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<?> updateMembershipState(@PathVariable UUID id, @RequestBody MembershipPatchStateDto membershipPatchStateDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.updateMembershipState(id, membershipPatchStateDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateMembershipRole(@PathVariable UUID id, @RequestBody MembershipPatchRoleDto membershipPatchRoleDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.updateMembershipRole(id, membershipPatchRoleDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMembership(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.deleteMembershipById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
