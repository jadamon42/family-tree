package com.github.jadamon42.family.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRequest {
    Optional<String> firstName;
    Optional<String> middleName;
    Optional<String> lastName;
    Optional<Sex> sex;
    Optional<LocalDate> birthDate;
    Optional<LocalDate> deathDate;
    Optional<UUID> parentsPartnershipId;
}
