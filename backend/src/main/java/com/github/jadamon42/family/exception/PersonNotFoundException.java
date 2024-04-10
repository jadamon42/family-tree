package com.github.jadamon42.family.exception;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(UUID id) {
        super("Person with id '" + id + "' not found.");
    }
}
