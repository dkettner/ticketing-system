package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.UUID;

@Repository
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public User addUser(User user) {
        if (userRepository.findByEmailEquals(user.getEmail()).isPresent()) {
            throw new UserException("user with email: " + user.getEmail().toString() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
                this.addUser(
                        new User(
                                "Obi-Wan Kenobi",
                                EmailAddress.fromString("hello.there@kenobi.com"),
                                "Schweineschnauze"
                        )).getId()
        );

        System.out.println(
                this.addUser(
                        new User(
                                "Ben",
                                EmailAddress.fromString("high_ground_rulz@negotiator.com"),
                                "Zitronensorbet"
                        )).getId()
        );
    }
}
