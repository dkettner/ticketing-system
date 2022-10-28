package com.kett.TicketSystem.domainprimitives;

import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.persistence.Embeddable;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@EqualsAndHashCode
public class EMailAddress {
    private String localPart;
    private String domain;

    protected EMailAddress(String localPart, String domain) {
        if (localPart == null || localPart.isEmpty()) {
            throw  new RuntimeException("localPart must not be null or empty");
        }
        if (domain == null || domain.isEmpty()) {
            throw  new RuntimeException("domain must not be null or empty");
        }

        this.localPart = localPart;
        this.domain = domain;
    }

    public static EMailAddress fromString(String eMailAddressCandidate) {
        if (eMailAddressCandidate == null || eMailAddressCandidate.isEmpty()) {
            throw  new RuntimeException("eMailAddressCandidate must not be null or empty");
        }
        if (!EmailValidator.getInstance().isValid(eMailAddressCandidate)) {
            throw new RuntimeException("eMailAddressCandidate is not in a valid format: " + eMailAddressCandidate);
        }

        String[] addressParts = eMailAddressCandidate.split("@", 2);
        return new EMailAddress(addressParts[0], addressParts[1]);
    }

    @Override
    public String toString() {
        return localPart + "@" + domain;
    }
}
