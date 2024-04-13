package com.github.jadamon42.family.model;

import java.time.LocalDate;
import java.util.UUID;

public interface PartnershipProjection {
    UUID getId();
    String getType();
    LocalDate getStartDate();
    LocalDate getEndDate();
}
