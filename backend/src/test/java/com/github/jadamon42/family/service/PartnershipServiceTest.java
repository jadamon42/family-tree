package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PartnershipServiceTest {
    private PartnershipRepository partnershipRepository;
    private PartnershipService partnershipService;
    private PersonRepository personRepository;

    @BeforeEach
    void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        partnershipRepository = Mockito.mock(PartnershipRepository.class);
        partnershipService = new PartnershipService(partnershipRepository, personRepository);
    }

    @Test
    void getPartnership() {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);

        when(partnershipRepository.findById(partnershipId.toString())).thenReturn(Optional.of(partnership));

        Optional<Partnership> result = partnershipService.getPartnership(partnershipId);

        assertThat(result).isEqualTo(Optional.of(partnership));
    }

    @Test
    void savePartnership() {
        Partnership partnership = new Partnership(null, "Marriage", null, null);

        partnershipService.savePartnership(partnership);

        verify(partnershipRepository).save(partnership);
    }

    @Test
    void savePartnershipWithPersonIds() {
        UUID personId1 = UUID.randomUUID();
        UUID personId2 = UUID.randomUUID();
        Person person1 = new Person(personId1.toString(), "John", "Doe", null, null);
        Person person2 = new Person(personId2.toString(), "Jane", "Doe", null, null);
        Partnership partnership = new Partnership(null, "Marriage", null, null);
        Partnership expectedPartnership = new Partnership("1", "Marriage", null, null);

        when(personRepository.findById(personId1.toString())).thenReturn(Optional.of(person1));
        when(personRepository.findById(personId2.toString())).thenReturn(Optional.of(person2));
        when(partnershipRepository.save(partnership)).thenReturn(expectedPartnership);

        partnershipService.savePartnership(partnership, List.of(personId1, personId2));

        verify(partnershipRepository).save(partnership);
        verify(personRepository).save(person1.withPartnership(expectedPartnership));
        verify(personRepository).save(person2.withPartnership(expectedPartnership));
    }

    @Test
    void savePartnershipWithPersonIdsWhenPersonNotFound() {
        UUID personId1 = UUID.randomUUID();
        UUID personId2 = UUID.randomUUID();
        Partnership partnership = new Partnership(null, "Marriage", null, null);

        when(personRepository.findById(personId1.toString())).thenReturn(Optional.empty());
        when(personRepository.findById(personId2.toString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.savePartnership(partnership, List.of(personId1, personId2)))
            .isInstanceOf(PersonNotFoundException.class)
            .hasMessage("Person with id '" + personId1 + "' not found.");
    }

    @Test
    void updatePartnership() {
        UUID partnershipId = UUID.randomUUID();
        UUID johnId = UUID.randomUUID();
        UUID janeId = UUID.randomUUID();
        UUID janetId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);
        Person john = new Person(johnId.toString(), "John", "Doe", List.of(partnership), null);
        Person jane = new Person(janeId.toString(), "Jane", "Doe", List.of(partnership), null);
        Person janet = new Person(janetId.toString(), "Janet", "Doe", null, null);

        when(personRepository.findById(johnId.toString())).thenReturn(Optional.of(john));
        when(personRepository.findById(janeId.toString())).thenReturn(Optional.of(jane));
        when(personRepository.findById(janetId.toString())).thenReturn(Optional.of(janet));
        when(personRepository.findPeopleByPartnershipId(partnershipId.toString())).thenReturn(List.of(john, jane));
        when(partnershipRepository.findById(partnership.getId())).thenReturn(Optional.of(partnership));
        when(partnershipRepository.save(partnership)).thenReturn(partnership);

        partnershipService.updatePartnership(partnershipId, partnership, List.of(johnId, janetId));

        verify(partnershipRepository).save(partnership);
        verify(personRepository).save(john);
        verify(personRepository).save(jane.withPartnerships(List.of()));
        verify(personRepository).save(janet.withPartnerships(List.of(partnership)));
    }

    @Test
    void updatePartnershipWhenPartnershipNotFound() {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);

        when(partnershipRepository.findById(partnership.getId())).thenReturn(Optional.empty());

        Optional<Partnership> result = partnershipService.updatePartnership(partnershipId, partnership, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void deletePartnership() {
        UUID partnershipId = UUID.randomUUID();
        UUID johnId = UUID.randomUUID();
        UUID janeId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);
        Person john = new Person(johnId.toString(), "John", "Doe", List.of(partnership), null);
        Person jane = new Person(janeId.toString(), "Jane", "Doe", List.of(partnership), null);

        when(personRepository.findPeopleByPartnershipId(partnershipId.toString())).thenReturn(List.of(john, jane));

        partnershipService.deletePartnership(partnershipId);

        verify(personRepository).save(john.withPartnerships(List.of()));
        verify(personRepository).save(jane.withPartnerships(List.of()));
        verify(partnershipRepository).deleteById(partnershipId.toString());
    }
}
