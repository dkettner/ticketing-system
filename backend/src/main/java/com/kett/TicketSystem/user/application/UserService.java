package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.UUID;

@Repository
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        addDefaultUsers();  // only for testing security
    }

    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NoUserFoundException("could not find user with id: " + id));
    }

    public User getUserByEMailAddress(EmailAddress eMailAddress) {
        return userRepository
                .findByEmailEquals(eMailAddress)
                .orElseThrow(() -> new NoUserFoundException("could not find user with eMailAddress: " + eMailAddress));
    }

    public boolean isExistentById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.getUserByEMailAddress(EmailAddress.fromString(email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail().toString(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    // only for testing security
    private void addDefaultUsers() {
        System.out.println(
                userRepository
                        .save(new User(
                                "Obi-Wan Kenobi",
                                EmailAddress.fromString("hello.there@kenobi.com"),
                                "Schweineschnauze"
                        )).getId()
        );

        System.out.println(
                userRepository
                        .save(new User(
                                "Ben",
                                EmailAddress.fromString("high_ground_rulz@negotiator.com"),
                                "Zitronensorbet"
                        )).getId()
        );
    }
}
