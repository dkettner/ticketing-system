package com.kett.TicketSystem.notification.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.common.exceptions.TooManyParametersException;
import com.kett.TicketSystem.notification.application.dto.NotificationPatchIsReadDto;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/notifications")
public class NotificationController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public NotificationController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        NotificationResponseDto notificationResponseDto = ticketSystemService.getNotificationById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(notificationResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByQuery(
            @RequestParam(name = "recipientId", required = false) UUID recipientId,
            @RequestParam(name = "email", required = false) String email
    ) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        if (recipientId != null && email != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<NotificationResponseDto> notificationResponseDtos;
        if (recipientId != null) {
            notificationResponseDtos = ticketSystemService.getNotificationsByRecipientId(recipientId);
        } else if (email != null) {
            notificationResponseDtos = ticketSystemService.getNotificationsByEmail(EmailAddress.fromString(email));
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }

        MDC.remove("transactionId");
        return new ResponseEntity<>(notificationResponseDtos, HttpStatus.OK);
    }

    @PatchMapping("/{id}/is-read")
    public ResponseEntity<?> patchNotificationReadState(@PathVariable UUID id, @RequestBody NotificationPatchIsReadDto notificationPatchIsReadDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.patchNotificationReadState(id, notificationPatchIsReadDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.deleteNotificationById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
