package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.MockPersonProjection;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
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
    @Test
    void getPartnership() {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);

        when(partnershipRepository.findById(partnershipId.toString())).thenReturn(Optional.of(partnership));

        Optional<Partnership> result = partnershipService.getPartnership(partnershipId);

        assertThat(result).isEqualTo(Optional.of(partnership));
    }

    @Test
    void savePartnershipWithPersonIds() {
        UUID personId1 = UUID.randomUUID();
        UUID personId2 = UUID.randomUUID();
        PersonProjection person1Projection = new MockPersonProjection(personId1.toString(), "John", "Doe", null);
        PersonProjection person2Projection = new MockPersonProjection(personId2.toString(), "Jane", "Doe", null);
        Person person1 = new Person(personId1.toString(), "John", "Doe", null, null);
        Person person2 = new Person(personId2.toString(), "Jane", "Doe", null, null);
        Partnership partnership = new Partnership(null, "Marriage", null, null);
        Partnership expectedPartnership = new Partnership("1", "Marriage", null, null);

        when(personRepository.findProjectionById(personId1.toString())).thenReturn(Optional.of(person1Projection));
        when(personRepository.findProjectionById(personId2.toString())).thenReturn(Optional.of(person2Projection));
        when(partnershipRepository.save(partnership)).thenReturn(expectedPartnership);

        partnershipService.savePartnership(partnership, List.of(personId1, personId2));

        verify(partnershipRepository).save(partnership);
        verify(personRepository).updateAndReturnProjection(personId1.toString(), person1.withPartnership(expectedPartnership));
        verify(personRepository).updateAndReturnProjection(personId2.toString(), person2.withPartnership(expectedPartnership));
    }

    @Test
    void savePartnershipWithPersonIdsFailsWhenPersonNotFound() {
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
    void savePartnershipFailsWhenNoPartnersProvided() {
        Partnership partnership = new Partnership(null, "Marriage", null, null);

        assertThatThrownBy(() -> partnershipService.savePartnership(partnership, List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("At least one partner must be provided.");
    }

    @Test
    void updatePartnership() {
        UUID partnershipId = UUID.randomUUID();
        UUID johnId = UUID.randomUUID();
        UUID janeId = UUID.randomUUID();
        UUID janetId = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);
        PersonProjection johnProjection = new MockPersonProjection(johnId.toString(), "John", "Doe", List.of(partnership));
        PersonProjection janeProjection = new MockPersonProjection(janeId.toString(), "Jane", "Doe", List.of(partnership));
        PersonProjection janetProjection = new MockPersonProjection(janetId.toString(), "Janet", "Doe", null);
        Person john = new Person(johnId.toString(), "John", "Doe", List.of(partnership), null);
        Person janet = new Person(janetId.toString(), "Janet", "Doe", null, null);

        when(personRepository.findProjectionById(johnId.toString())).thenReturn(Optional.of(johnProjection));
        when(personRepository.findProjectionById(janeId.toString())).thenReturn(Optional.of(janeProjection));
        when(personRepository.findProjectionById(janetId.toString())).thenReturn(Optional.of(janetProjection));
        when(personRepository.findPersonIdsByPartnershipId(partnershipId.toString())).thenReturn(List.of(johnId.toString(), janeId.toString()));
        when(partnershipRepository.findById(partnership.getId())).thenReturn(Optional.of(partnership));
        when(partnershipRepository.save(partnership)).thenReturn(partnership);

        partnershipService.updatePartnership(partnershipId, partnership, List.of(johnId, janetId));

        verify(partnershipRepository).save(partnership);
        verify(personRepository).removeFromPartnership(janeId.toString(), partnershipId.toString());
        verify(personRepository).updateAndReturnProjection(johnId.toString(), john);
        verify(personRepository).updateAndReturnProjection(janetId.toString(), janet.withPartnership(partnership));
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

        when(personRepository.findPersonIdsByPartnershipId(partnershipId.toString())).thenReturn(List.of(johnId.toString(), janeId.toString()));

        partnershipService.deletePartnership(partnershipId);

        verify(personRepository).removeAllFromPartnership(partnershipId.toString());
        verify(partnershipRepository).deleteById(partnershipId.toString());
    }

    static private PartnershipRepository partnershipRepository;
    static private PartnershipService partnershipService;
    static private PersonRepository personRepository;

    @BeforeAll
    static void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        partnershipRepository = Mockito.mock(PartnershipRepository.class);
        partnershipService = new PartnershipService(partnershipRepository, personRepository);
    }
}
