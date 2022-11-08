package com.kett.TicketSystem.authentication;

import com.kett.TicketSystem.authentication.jwt.JwtTokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProviderService jwtTokenProviderService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenProviderService jwtTokenProviderService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProviderService = jwtTokenProviderService;
    }


    public String authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtTokenProviderService.generateToken(authentication);
    }
}
