package com.kett.TicketSystem.authentication.application;

import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import com.kett.TicketSystem.authentication.domain.AuthenticationDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationApplicationService {
    private final AuthenticationDomainService authenticationDomainService;

    @Autowired
    public AuthenticationApplicationService(AuthenticationDomainService authenticationDomainService) {
        this.authenticationDomainService = authenticationDomainService;
    }

    // authentication
    public String authenticateUser(AuthenticationPostDto authenticationPostDto) {
        return authenticationDomainService
                .authenticateUser(authenticationPostDto.getEmail(), authenticationPostDto.getPassword());
    }
}
