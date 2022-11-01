package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.application.TicketSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"})
@RequestMapping("/memberships")
public class MembershipController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public MembershipController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }
}
