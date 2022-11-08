package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"})
@RequestMapping("/users")
public class UserController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public UserController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    // endpoints

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID id) {
        UserResponseDto userResponseDto = ticketSystemService.getUserById(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getUserByQuery(
            @RequestParam(name = "email", required = true) String email
    ) {
        if (email == null) {
            throw new NoParametersException("cannot query if no parameters are specified");
        }
        UserResponseDto userResponseDto = ticketSystemService.getByEMailAddress(EmailAddress.fromString(email));
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserPostDto userPostDto) {
        UserResponseDto userResponseDto = ticketSystemService.addUser(userPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(userResponseDto);
    }
}
