package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.common.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.exceptions.EmailAlreadyInUseException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserService(
            UserRepository userRepository,
            MembershipService membershipService,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.membershipService = membershipService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }


    // create

    public User addUser(User user) throws EmailAlreadyInUseException {
        if (userRepository.existsByEmailEquals(user.getEmail())) {
            throw new EmailAlreadyInUseException("email: " + user.getEmail().toString() + " is already in use.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User initializedUser = userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(initializedUser.getId()));
        return initializedUser;
    }


    // read

    public User getUserById(UUID id) throws NoUserFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NoUserFoundException("could not find user with id: " + id));
    }

    public User getUserByEMailAddress(EmailAddress eMailAddress) throws NoUserFoundException {
        return userRepository
                .findByEmailEquals(eMailAddress)
                .orElseThrow(() -> new NoUserFoundException("could not find user with eMailAddress: " + eMailAddress));
    }

    public UUID getUserIdByEmail(EmailAddress postingUserEmail) throws NoUserFoundException {
        return this.getUserByEMailAddress(postingUserEmail).getId();
    }

    public UUID getUserIdByEmail(String postingUserEmail) throws NoUserFoundException {
        return this.getUserIdByEmail(EmailAddress.fromString(postingUserEmail));
    }

    public boolean isExistentById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws NoUserFoundException, UsernameNotFoundException {
        User user = this.getUserByEMailAddress(EmailAddress.fromString(email));
        List<GrantedAuthority> grantedAuthorities = this.getAllUserAuthoritiesByUserId(user.getId());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail().toString(),
                user.getPassword(),
                grantedAuthorities
        );
    }

    private List<GrantedAuthority> getAllUserAuthoritiesByUserId(UUID id) {
        List<GrantedAuthority> projectAuthorities = membershipService.getProjectAuthoritiesByUserId(id);
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER_" + id);

        List<GrantedAuthority> allGrantedAuthorities = new ArrayList<>();
        allGrantedAuthorities.add(userAuthority);
        allGrantedAuthorities.addAll(projectAuthorities);

        return allGrantedAuthorities;
    }


    // update

    public void patchUserById(UUID id, String name, EmailAddress email) throws UserException, NoUserFoundException {
        User user = this.getUserById(id);

        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            if (userRepository.existsByEmailEquals(email)) {
                throw new EmailAlreadyInUseException("New email: " + email.toString() + " is already in use.");
            }
            user.setEmail(email);
        }

        userRepository.save(user);
    }


    // delete

    public void deleteById(UUID id) {
        Long numOfDeletedUsers = userRepository.removeById(id);

        if (numOfDeletedUsers == 0) {
            throw new NoUserFoundException("could not delete because there was no user with id: " + id);
        } else if (numOfDeletedUsers > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple users were deleted when deleting user with id: " + id
            );
        } else {
            eventPublisher.publishEvent(new UserDeletedEvent(id));
        }
    }
}
