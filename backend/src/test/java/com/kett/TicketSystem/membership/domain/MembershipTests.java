package com.kett.TicketSystem.membership.domain;

import com.kett.TicketSystem.common.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({ "test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MembershipTests {
    private final MembershipRepository membershipRepository;

    private UUID uuid0;
    private UUID uuid1;
    private UUID uuid2;
    private UUID uuid3;

    private Role standardRole;
    private Role adminRole;

    private Membership membership0;
    private Membership membership1;
    private Membership membership2;
    private Membership membership3;

    @Autowired
    public MembershipTests(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @BeforeEach
    public void buildUp() {
        uuid0 = UUID.randomUUID();
        uuid1 = UUID.randomUUID();
        uuid2 = UUID.randomUUID();
        uuid3 = UUID.randomUUID();

        standardRole = Role.MEMBER;
        adminRole = Role.ADMIN;

        membership0 = new Membership(uuid0, uuid1, standardRole);
        membership1 = new Membership(uuid1, uuid2, standardRole);
        membership2 = new Membership(uuid2, uuid3, adminRole);
        membership3 = new Membership(uuid3, uuid0, adminRole);
    }

    @AfterEach
    public void tearDown() {
        uuid0 = null;
        uuid1 = null;
        uuid2 = null;
        uuid3 = null;

        standardRole = null;
        adminRole = null;

        membership0 = null;
        membership1 = null;
        membership2 = null;
        membership3 = null;

        membershipRepository.deleteAll();
    }

    @Test
    public void checkValidConstructorParameters() {
        new Membership(uuid0, uuid1, standardRole);
        new Membership(uuid1, uuid2, standardRole);
        new Membership(uuid2, uuid3, adminRole);
        new Membership(uuid3, uuid0, adminRole);
    }

    @Test
    public void checkNullConstructorParameters() {
        assertThrows(MembershipException.class, () -> new Membership(null, null, null));
        assertThrows(MembershipException.class, () -> new Membership(null, null, standardRole));
        assertThrows(MembershipException.class, () -> new Membership(null, uuid0, null));
        assertThrows(MembershipException.class, () -> new Membership(null, uuid1, adminRole));
        assertThrows(MembershipException.class, () -> new Membership(uuid2, null, null));
        assertThrows(MembershipException.class, () -> new Membership(uuid3, null, standardRole));
        assertThrows(MembershipException.class, () -> new Membership(uuid0, uuid1, null));
    }

    @Test
    public void checkEquals() {
        // without id
        assertEquals(membership0, membership0);
        assertEquals(membership1, membership1);
        assertEquals(membership2, membership2);
        assertEquals(membership3, membership3);

        // add id
        membershipRepository.save(membership0);
        membershipRepository.save(membership1);
        membershipRepository.save(membership2);
        membershipRepository.save(membership3);

        // with id
        assertEquals(membership0, membership0);
        assertEquals(membership1, membership1);
        assertEquals(membership2, membership2);
        assertEquals(membership3, membership3);
    }

    @Test
    public void checkNotEquals() {
        Membership membership0Copy = new Membership(uuid0, uuid1, standardRole);
        Membership membership1Copy = new Membership(uuid1, uuid2, standardRole);
        Membership membership2Copy = new Membership(uuid2, uuid3, adminRole);
        Membership membership3Copy = new Membership(uuid3, uuid0, adminRole);

        // without id
        assertNotEquals(membership0, membership0Copy);
        assertNotEquals(membership1, membership1Copy);
        assertNotEquals(membership2, membership2Copy);
        assertNotEquals(membership3, membership3Copy);

        assertNotEquals(membership0, membership1);
        assertNotEquals(membership1, membership2);
        assertNotEquals(membership2, membership3);
        assertNotEquals(membership3, membership0);

        // add id
        membershipRepository.save(membership0);
        membershipRepository.save(membership1);
        membershipRepository.save(membership2);
        membershipRepository.save(membership3);
        membershipRepository.save(membership0Copy);
        membershipRepository.save(membership1Copy);
        membershipRepository.save(membership2Copy);
        membershipRepository.save(membership3Copy);

        // with id
        assertNotEquals(membership0, membership0Copy);
        assertNotEquals(membership1, membership1Copy);
        assertNotEquals(membership2, membership2Copy);
        assertNotEquals(membership3, membership3Copy);

        assertNotEquals(membership0, membership1);
        assertNotEquals(membership1, membership2);
        assertNotEquals(membership2, membership3);
        assertNotEquals(membership3, membership0);
    }

    @Test
    public void checkProjectId() {
        assertEquals(uuid0, membership0.getProjectId());
        assertEquals(uuid1, membership1.getProjectId());
        assertEquals(uuid2, membership2.getProjectId());
        assertEquals(uuid3, membership3.getProjectId());

        assertNotEquals(uuid1, membership0.getProjectId());
        assertNotEquals(uuid2, membership1.getProjectId());
        assertNotEquals(uuid3, membership2.getProjectId());
        assertNotEquals(uuid0, membership3.getProjectId());
    }

    @Test
    public void checkUserId() {
        assertEquals(uuid1, membership0.getUserId());
        assertEquals(uuid2, membership1.getUserId());
        assertEquals(uuid3, membership2.getUserId());
        assertEquals(uuid0, membership3.getUserId());

        assertNotEquals(uuid0, membership0.getUserId());
        assertNotEquals(uuid1, membership1.getUserId());
        assertNotEquals(uuid2, membership2.getUserId());
        assertNotEquals(uuid3, membership3.getUserId());
    }

    @Test
    public void checkRole() {
        assertEquals(Role.MEMBER, membership0.getRole());
        assertEquals(Role.MEMBER, membership1.getRole());
        assertEquals(Role.ADMIN, membership2.getRole());
        assertEquals(Role.ADMIN, membership3.getRole());

        assertNotEquals(Role.ADMIN, membership0.getRole());
        assertNotEquals(Role.ADMIN, membership1.getRole());
        assertNotEquals(Role.MEMBER, membership2.getRole());
        assertNotEquals(Role.MEMBER, membership3.getRole());
    }

    @Test
    public void checkSetRoleNull() {
        assertThrows(MembershipException.class, () -> membership0.setRole(null));
        assertThrows(MembershipException.class, () -> membership1.setRole(null));
        assertThrows(MembershipException.class, () -> membership2.setRole(null));
        assertThrows(MembershipException.class, () -> membership3.setRole(null));
    }

    @Test
    public void checkDefaultState() {
        assertEquals(State.OPEN, membership0.getState());
        assertEquals(State.OPEN, membership1.getState());
        assertEquals(State.OPEN, membership2.getState());
        assertEquals(State.OPEN, membership3.getState());
    }

    @Test
    public void checkSetStateFromOpenToAccepted() {
        membership0.setState(State.ACCEPTED);
        membership1.setState(State.ACCEPTED);
        membership2.setState(State.ACCEPTED);
        membership3.setState(State.ACCEPTED);
    }

    @Test
    public void checkSetStateOpenAfterAccepted() {
        membership0.setState(State.ACCEPTED);
        membership1.setState(State.ACCEPTED);
        membership2.setState(State.ACCEPTED);
        membership3.setState(State.ACCEPTED);

        assertThrows(IllegalStateUpdateException.class, () -> membership0.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership1.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership2.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership3.setState(State.OPEN));
    }

    @Test
    public void checkSetStateSame() {
        assertThrows(IllegalStateUpdateException.class, () -> membership0.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership1.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership2.setState(State.OPEN));
        assertThrows(IllegalStateUpdateException.class, () -> membership3.setState(State.OPEN));

        membership0.setState(State.ACCEPTED);
        membership1.setState(State.ACCEPTED);
        membership2.setState(State.ACCEPTED);
        membership3.setState(State.ACCEPTED);

        assertThrows(IllegalStateUpdateException.class, () -> membership0.setState(State.ACCEPTED));
        assertThrows(IllegalStateUpdateException.class, () -> membership1.setState(State.ACCEPTED));
        assertThrows(IllegalStateUpdateException.class, () -> membership2.setState(State.ACCEPTED));
        assertThrows(IllegalStateUpdateException.class, () -> membership3.setState(State.ACCEPTED));
    }
}
