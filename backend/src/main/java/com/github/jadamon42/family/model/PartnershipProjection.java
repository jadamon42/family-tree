package com.github.jadamon42.family.model;

import java.time.LocalDate;

public interface PartnershipProjection {
    String getId();
    String getType();
    LocalDate getStartDate();
    LocalDate getEndDate();
}
