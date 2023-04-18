package com.kett.TicketSystem.authentication.domain;

import com.kett.TicketSystem.authentication.domain.events.UserAuthenticatedEvent;
import com.kett.TicketSystem.authentication.domain.jwt.JwtTokenProvider;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationDomainService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthenticationDomainService(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            ApplicationEventPublisher eventPublisher
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.eventPublisher = eventPublisher;
    }


    public String authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        String jwtToken = jwtTokenProvider.generateToken(authentication);
        eventPublisher.publishEvent(new UserAuthenticatedEvent(EmailAddress.fromString(email)));
        return jwtToken;
    }
}
