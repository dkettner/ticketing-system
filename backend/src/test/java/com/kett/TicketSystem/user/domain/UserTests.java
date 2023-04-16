package com.kett.TicketSystem.user.domain;

import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserTests {
    private final UserRepository userRepository;

    @Autowired
    public UserTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String validName0;
    private String validName1;
    private String validName2;
    private String validName3;

    private String emptyName;
    private String nullName;

    private String validEmail;
}
