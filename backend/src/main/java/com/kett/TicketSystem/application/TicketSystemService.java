package com.kett.TicketSystem.application;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserPatchDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketSystemService {
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Autowired
    public TicketSystemService (
            UserService userService,
            DtoMapper dtoMapper
    ) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    // user

    public UserResponseDto getUserById(UUID id) {
        User user = userService.getUserById(id);
        return dtoMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto getByEMailAddress(EmailAddress eMailAddress) {
        User user = userService.getUserByEMailAddress(eMailAddress);
        return dtoMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto addUser(UserPostDto userPostDto) {
        User user = userService.addUser(
                dtoMapper.mapUserPostDtoToUser(userPostDto)
        );

        return dtoMapper.mapUserToUserResponseDto(user);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#id))")
    public void patchUserById(UUID id, UserPatchDto userPatchDto) {
        userService.patchUserById(
                id,
                userPatchDto.getName(),
                userPatchDto.getEmail()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#id))")
    public void deleteUserById(UUID id) {
        userService.deleteById(id);
    }
}
