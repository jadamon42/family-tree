package com.github.jadamon42.family.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PartnershipTest {

    @Test
    void withId() {
        Partnership partnership = new Partnership(null, "Marriage", null, null);
        Partnership partnershipWithId = partnership.withId("00000000-0000-0000-0000-000000000001");
        assertThat(partnershipWithId.getId()).isEqualTo("00000000-0000-0000-0000-000000000001");
    }

    @Test
    void withType() {
        Partnership partnership = new Partnership(null, "Cohabitation", null, null);
        Partnership partnershipWithType = partnership.withType("Marriage");
        assertThat(partnershipWithType.getType()).isEqualTo("Marriage");
    }

    @Test
    void withStartDate() {
        Partnership partnership = new Partnership(null, "Marriage", null, null);
        Partnership partnershipWithStartDate = partnership.withStartDate(LocalDate.of(2024, 1, 1));
        assertThat(partnershipWithStartDate.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void withEndDate() {
        Partnership partnership = new Partnership(null, "Marriage", LocalDate.of(2024,1,1), null);
        Partnership partnershipWithEndDate = partnership.withEndDate(LocalDate.of(2024, 12, 31));
        assertThat(partnershipWithEndDate.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    }
}
