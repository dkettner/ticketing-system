package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserService {
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
