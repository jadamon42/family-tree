package com.github.jadamon42.family.model;

public interface PersonProjection {
    String getId();
    String getFirstName();
    String getLastName();
    Iterable<PartnershipProjection> getPartnerships();
}
