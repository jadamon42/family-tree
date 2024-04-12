package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@AllArgsConstructor
@Builder
public class MockPersonProjection implements PersonProjection {
    String id;
    String firstName;
    String lastName;
    String sex;
    LocalDate birthDate;
    LocalDate deathDate;
    List<PartnershipProjection> partnerships;
}
