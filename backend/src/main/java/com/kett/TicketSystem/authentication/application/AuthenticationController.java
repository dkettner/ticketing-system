package com.kett.TicketSystem.authentication.application;

import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@Transactional
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/authentications")
public class AuthenticationController {
    private final AuthenticationApplicationService authenticationApplicationService;

    @Autowired
    public AuthenticationController(AuthenticationApplicationService authenticationApplicationService) {
        this.authenticationApplicationService = authenticationApplicationService;
    }

    @PostMapping
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationPostDto authenticationPostDto) {
        String jwtValue = authenticationApplicationService.authenticateUser(authenticationPostDto);
        return ResponseEntity.ok(jwtValue);
    }
}
