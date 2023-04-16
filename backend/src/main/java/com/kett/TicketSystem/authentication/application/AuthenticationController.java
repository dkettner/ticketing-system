package com.kett.TicketSystem.authentication.application;

import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;



@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationApplicationService authenticationApplicationService;

    @Autowired
    public AuthenticationController(AuthenticationApplicationService authenticationApplicationService) {
        this.authenticationApplicationService = authenticationApplicationService;
    }


    @PostMapping
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationPostDto authenticationPostDto) {
        String jwtValue = authenticationApplicationService.authenticateUser(authenticationPostDto);
        ResponseCookie responseCookie =
                ResponseCookie
                        .from("jwt", jwtValue)
                        .maxAge(12*60*60) // 12 hours
                        .path("/")
                        .sameSite("None")   // TODO: in production -> SameSite=Lax
                        .httpOnly(true)
                        .secure(true)
                        .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}
