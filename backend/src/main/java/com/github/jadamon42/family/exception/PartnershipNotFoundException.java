package com.github.jadamon42.family.exception;

import java.util.UUID;

public class PartnershipNotFoundException extends RuntimeException {
    public PartnershipNotFoundException(UUID parentsPartnershipId) {
        super("Partnership with id '" + parentsPartnershipId + "' does not exist.");
    }
}
