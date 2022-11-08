package com.kett.TicketSystem.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPostDto {
    // TODO: Add UserCreationToken
    private String name;
    private String email;
    private String password;
}
