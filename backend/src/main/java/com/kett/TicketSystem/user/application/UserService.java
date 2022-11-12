package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembershipService membershipService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MembershipService membershipService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.membershipService = membershipService;
    }


    // create

    public User addUser(User user) throws UserException {
        if (userRepository.findByEmailEquals(user.getEmail()).isPresent()) {
            throw new UserException("user with email: " + user.getEmail().toString() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
        List<GrantedAuthority> grantedAuthorities = membershipService.getProjectAuthoritiesByUserId(id);
        grantedAuthorities.add( (GrantedAuthority) () -> "ROLE_" + "USER_" + id );

        return grantedAuthorities;
    }


    // update


    // delete


}
