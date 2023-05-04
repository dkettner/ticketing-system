package com.kett.TicketSystem.notification.application;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.common.exceptions.TooManyParametersException;
import com.kett.TicketSystem.notification.application.dto.NotificationPatchDto;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationApplicationService notificationApplicationService;

    @Autowired
    public NotificationController(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable UUID id) {
        NotificationResponseDto notificationResponseDto = notificationApplicationService.getNotificationById(id);
        return new ResponseEntity<>(notificationResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByQuery(
            @RequestParam(name = "recipientId", required = false) UUID recipientId,
            @RequestParam(name = "email", required = false) String email
    ) {
        if (recipientId != null && email != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<NotificationResponseDto> notificationResponseDtos;
        if (recipientId != null) {
            notificationResponseDtos = notificationApplicationService.getNotificationsByRecipientId(recipientId);
        } else if (email != null) {
            notificationResponseDtos = notificationApplicationService.getNotificationsByEmail(EmailAddress.fromString(email));
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }

        return new ResponseEntity<>(notificationResponseDtos, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchNotification(@PathVariable UUID id, @RequestBody NotificationPatchDto notificationPatchDto) {
        notificationApplicationService.patchNotification(id, notificationPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable UUID id) {
        notificationApplicationService.deleteNotificationById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
