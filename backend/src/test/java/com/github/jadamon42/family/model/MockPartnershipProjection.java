package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@AllArgsConstructor
@Builder
public class MockPartnershipProjection implements PartnershipProjection {
    UUID id;
    String type;
    LocalDate startDate;
    LocalDate endDate;
}
