package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
@AllArgsConstructor
@Builder
public class MockPersonProjection implements PersonProjection {
    UUID id;
    String firstName;
    String lastName;
    Sex sex;
    LocalDate birthDate;
    LocalDate deathDate;
    List<PartnershipProjection> partnerships;
}
