package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@Builder
public class MockPartnershipProjection implements PartnershipProjection {
    String id;
    String type;
    LocalDate startDate;
    LocalDate endDate;
}
