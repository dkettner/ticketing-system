package com.kett.TicketSystem.user.repository;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailEquals(EmailAddress email);
    Boolean existsByEmailEquals(EmailAddress email);

    Long removeById(UUID id);
}
