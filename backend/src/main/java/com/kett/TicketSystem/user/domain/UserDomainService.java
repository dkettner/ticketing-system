package com.kett.TicketSystem.user.domain;

import com.kett.TicketSystem.membership.domain.MembershipDomainService;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.common.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserDomainService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MembershipDomainService membershipDomainService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserDomainService(
            UserRepository userRepository,
            MembershipDomainService membershipDomainService,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.membershipDomainService = membershipDomainService;
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
        eventPublisher.publishEvent(new UserCreatedEvent(initializedUser.getId(), initializedUser.getName(), initializedUser.getEmail()));
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
        List<GrantedAuthority> projectAuthorities = membershipDomainService.getProjectAuthoritiesByUserId(id);
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER_" + id);

        List<GrantedAuthority> allGrantedAuthorities = new ArrayList<>();
        allGrantedAuthorities.add(userAuthority);
        allGrantedAuthorities.addAll(projectAuthorities);

        return allGrantedAuthorities;
    }


    // update

    public void patchUserById(UUID id, String name, String email) throws UserException, NoUserFoundException {
        User user = this.getUserById(id);

        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            EmailAddress emailAddress = EmailAddress.fromString(email);
            if (userRepository.existsByEmailEquals(emailAddress)) {
                throw new EmailAlreadyInUseException("New email: " + emailAddress.toString() + " is already in use.");
            }
            user.setEmail(emailAddress);
        }
        userRepository.save(user);
        eventPublisher.publishEvent(new UserPatchedEvent(user.getId(), user.getName(), user.getEmail()));
    }


    // delete

    public void deleteById(UUID id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NoUserFoundException("could not delete because there was no user with id: " + id));
        userRepository.removeById(id);
        eventPublisher.publishEvent(new UserDeletedEvent(user.getId(), user.getName(), user.getEmail()));
    }
}
