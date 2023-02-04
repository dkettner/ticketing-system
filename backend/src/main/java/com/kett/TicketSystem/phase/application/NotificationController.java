package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
        NotificationResponseDto notificationResponseDto = ticketSystemService.getNotificationById(id);
        return new ResponseEntity<>(notificationResponseDto, HttpStatus.OK);
    }

}
