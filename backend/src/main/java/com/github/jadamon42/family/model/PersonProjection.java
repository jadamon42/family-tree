package com.github.jadamon42.family.model;

import java.util.List;

public interface PersonProjection {
    String getId();
    String getFirstName();
    String getLastName();
    List<PartnershipProjection> getPartnerships();
}
