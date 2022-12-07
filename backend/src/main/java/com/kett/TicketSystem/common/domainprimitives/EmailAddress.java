package com.kett.TicketSystem.common.domainprimitives;

import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.persistence.Embeddable;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@EqualsAndHashCode
public class EmailAddress {
    private String localPart;
    private String domain;

    protected EmailAddress(String localPart, String domain) {
        if (localPart == null || localPart.isEmpty()) {
            throw  new EmailAddressException("localPart must not be null or empty");
        }
        if (domain == null || domain.isEmpty()) {
            throw  new EmailAddressException("domain must not be null or empty");
        }

        this.localPart = localPart;
        this.domain = domain;
    }

    public static EmailAddress fromString(String eMailAddressCandidate) {
        if (eMailAddressCandidate == null || eMailAddressCandidate.isEmpty()) {
            throw new EmailAddressException("eMailAddressCandidate must not be null or empty");
        }
        if (!EmailValidator.getInstance().isValid(eMailAddressCandidate)) {
            throw new EmailAddressException("eMailAddressCandidate is not in a valid format: " + eMailAddressCandidate);
        }

        String[] addressParts = eMailAddressCandidate.split("@", 2);
        return new EmailAddress(addressParts[0], addressParts[1]);
    }

    @Override
    public String toString() {
        return localPart + "@" + domain;
    }
}
