package com.github.jadamon42.family.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    void withPartnershipWhenNoCurrentPartnerships() {
        Partnership partnership = new Partnership("1", "Marriage", null, null);
        Person person = new Person("1", "John", "Doe", null, null);
        Person personWithPartnership = person.withPartnership(partnership);
        assertThat(personWithPartnership.getPartnerships()).containsExactly(partnership);
    }

    @Test
    void withPartnershipWhenCurrentPartnerships() {
        Partnership partnership1 = new Partnership("1", "Marriage", null, null);
        Partnership partnership2 = new Partnership("2", "Marriage", null, null);
        Person person = new Person("1", "John", "Doe", List.of(partnership1), null);
        Person personWithPartnership = person.withPartnership(partnership2);
        assertThat(personWithPartnership.getPartnerships()).containsExactly(partnership1, partnership2);
    }

    @Test
    void withChildWhenNoCurrentChildren() {
        Person child = new Person(null, "Jane", "Doe", null, null);
        Person person = new Person(null, "John", "Doe", null, null);
        Person personWithChild = person.withChild(child);
        assertThat(personWithChild.getChildren()).containsExactly(child);
    }

    @Test
    void withChildWhenCurrentChildren() {
        Person child1 = new Person(null, "Jane", "Doe", null, null);
        Person child2 = new Person(null, "Jane", "Doe", null, null);
        Person person = new Person(null, "John", "Doe", null, List.of(child1));
        Person personWithChild = person.withChild(child2);
        assertThat(personWithChild.getChildren()).containsExactly(child1, child2);
    }

    @Test
    void withId() {
        Person person = new Person(null, "John", "Doe", null, null);
        Person personWithId = person.withId("00000000-0000-0000-0000-000000000001");
        assertThat(personWithId.getId()).isEqualTo("00000000-0000-0000-0000-000000000001");
    }

    @Test
    void withFirstName() {
        Person person = new Person(null, "John", "Doe", null, null);
        Person personWithFirstName = person.withFirstName("Jane");
        assertThat(personWithFirstName.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void withLastName() {
        Person person = new Person(null, "John", "Doe", null, null);
        Person personWithLastName = person.withLastName("Doe");
        assertThat(personWithLastName.getLastName()).isEqualTo("Doe");
    }

    @Test
    void withPartnerships() {
        Partnership partnership1 = new Partnership("1", "Marriage", null, null);
        Partnership partnership2 = new Partnership("2", "Marriage", null, null);
        Person person = new Person("1", "John", "Doe", List.of(partnership1), null);
        Person personWithPartnerships = person.withPartnerships(List.of(partnership2));
        assertThat(personWithPartnerships.getPartnerships()).containsExactly(partnership2);
    }

    @Test
    void withChildren() {
        Person child1 = new Person(null, "Jane", "Doe", null, null);
        Person child2 = new Person(null, "Jane", "Doe", null, null);
        Person person = new Person(null, "John", "Doe", null, List.of(child1));
        Person personWithChildren = person.withChildren(List.of(child2));
        assertThat(personWithChildren.getChildren()).containsExactly(child2);
    }

    @Test
    void withPartnershipWhenAlreadyContainsPartnershipWithSameId() {
        Partnership partnership1 = new Partnership("1", "Marriage", LocalDate.of(2024,1,1), null);
        Partnership partnership2 = new Partnership("1", "Marriage", LocalDate.of(2024,1,1), LocalDate.of(2024,12,31));
        Person person = new Person("1", "John", "Doe", List.of(partnership1), null);
        Person personWithPartnership = person.withPartnership(partnership2);
        assertThat(personWithPartnership.getPartnerships()).containsExactly(partnership2);
    }

    @Test
    void withChildWhenAlreadyContainsChildWithSameId() {
        Person child1 = new Person("1", "Jane", "Doe", null, null);
        Person child2 = new Person("1", "Janet", "Doe", null, null);
        Person person = new Person("1", "John", "Doe", null, List.of(child1));
        Person personWithChild = person.withChild(child2);
        assertThat(personWithChild.getChildren()).containsExactly(child2);
    }
}
