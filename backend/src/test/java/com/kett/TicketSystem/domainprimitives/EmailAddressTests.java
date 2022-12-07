package com.kett.TicketSystem.domainprimitives;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.domainprimitives.EmailAddressException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailAddressTests {
    private String validLocalPart0;
    private String validLocalPart1;
    private String validLocalPart2;
    private String validLocalPart3;

    private String validDomain0;
    private String validDomain1;
    private String validDomain2;
    private String validDomain3;

    private String validAddress0;
    private String validAddress1;
    private String validAddress2;
    private String validAddress3;

    private String invalidAddress0;
    private String invalidAddress1;
    private String invalidAddress2;
    private String invalidAddress3;
    private String invalidAddress4;
    private String invalidAddress5;

    @BeforeEach
    public void buildUp() {
        validLocalPart0 = "john.doe";
        validLocalPart1 = "katharinavanpoorten";
        validLocalPart2 = "elon_musk-the-real-one";
        validLocalPart3 = "nedStark";

        validDomain0 = "gmail.com";
        validDomain1 = "web.de";
        validDomain2 = "tesla.com";
        validDomain3 = "soiaf.org";

        validAddress0 = validLocalPart0 + "@" + validDomain0;
        validAddress1 = validLocalPart1 + "@" + validDomain1;
        validAddress2 = validLocalPart2 + "@" + validDomain2;
        validAddress3 = validLocalPart3 + "@" + validDomain3;

        invalidAddress0 = null;
        invalidAddress1 = "hansZimmer.de";
        invalidAddress2 = "Wolfgang@Amadeus@Mozart.fun";
        invalidAddress3 = "just\"not\"right@example.com";
        invalidAddress4 = "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com";
        invalidAddress5 = "email@addressmustcontainadomainatheend";
    }

    @AfterEach
    public void tearDown() {
        validLocalPart0 = null;
        validLocalPart1 = null;
        validLocalPart2 = null;
        validLocalPart3 = null;

        validDomain0 = null;
        validDomain1 = null;
        validDomain2 = null;
        validDomain3 = null;

        validAddress0 = null;
        validAddress1 = null;
        validAddress2 = null;
        validAddress3 = null;

        invalidAddress0 = null;
        invalidAddress1 = null;
        invalidAddress2 = null;
        invalidAddress3 = null;
        invalidAddress4 = null;
        invalidAddress5 = null;
    }

    @Test
    public void checkValidEmailAddress() {
        EmailAddress.fromString(validAddress0);
        EmailAddress.fromString(validAddress1);
        EmailAddress.fromString(validAddress2);
        EmailAddress.fromString(validAddress3);
    }

    @Test
    public void checkInvalidEmailAddress() {
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress0));
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress1));
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress2));
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress3));
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress4));
        assertThrows(EmailAddressException.class, () -> EmailAddress.fromString(invalidAddress5));
    }

    @Test
    public void checkEquals() {
        assertEquals(EmailAddress.fromString(validAddress0), EmailAddress.fromString(validAddress0));
        assertEquals(EmailAddress.fromString(validAddress1), EmailAddress.fromString(validAddress1));
        assertEquals(EmailAddress.fromString(validAddress2), EmailAddress.fromString(validAddress2));
        assertEquals(EmailAddress.fromString(validAddress3), EmailAddress.fromString(validAddress3));
    }

    @Test
    public void checkNotEquals() {
        assertNotEquals(EmailAddress.fromString(validAddress0), EmailAddress.fromString(validAddress1));
        assertNotEquals(EmailAddress.fromString(validAddress0), EmailAddress.fromString(validAddress2));
        assertNotEquals(EmailAddress.fromString(validAddress1), EmailAddress.fromString(validAddress2));
        assertNotEquals(EmailAddress.fromString(validAddress1), EmailAddress.fromString(validAddress3));
        assertNotEquals(EmailAddress.fromString(validAddress2), EmailAddress.fromString(validAddress3));
        assertNotEquals(EmailAddress.fromString(validAddress2), EmailAddress.fromString(validAddress0));
    }

    @Test
    public void checkToString() {
        assertEquals(validAddress0, EmailAddress.fromString(validAddress0).toString());
        assertEquals(validAddress1, EmailAddress.fromString(validAddress1).toString());
        assertEquals(validAddress2, EmailAddress.fromString(validAddress2).toString());
        assertEquals(validAddress3, EmailAddress.fromString(validAddress3).toString());
    }

    @Test
    public void checkLocalPart() {
        assertEquals(validLocalPart0, EmailAddress.fromString(validAddress0).getLocalPart());
        assertEquals(validLocalPart1, EmailAddress.fromString(validAddress1).getLocalPart());
        assertEquals(validLocalPart2, EmailAddress.fromString(validAddress2).getLocalPart());
        assertEquals(validLocalPart3, EmailAddress.fromString(validAddress3).getLocalPart());
    }

    @Test
    public void checkDomain() {
        assertEquals(validDomain0, EmailAddress.fromString(validAddress0).getDomain());
        assertEquals(validDomain1, EmailAddress.fromString(validAddress1).getDomain());
        assertEquals(validDomain2, EmailAddress.fromString(validAddress2).getDomain());
        assertEquals(validDomain3, EmailAddress.fromString(validAddress3).getDomain());
    }
}
