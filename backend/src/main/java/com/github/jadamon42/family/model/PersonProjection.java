package com.github.jadamon42.family.model;

import java.time.LocalDate;
import java.util.List;

public interface PersonProjection {
    String getId();
    String getFirstName();
    String getLastName();
    String getSex();
    LocalDate getBirthDate();
    LocalDate getDeathDate();
    List<PartnershipProjection> getPartnerships();
}
