package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.MockPersonProjection;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonServiceTest {
    @Test
    void getPerson() {
        UUID id = UUID.randomUUID();
        PersonProjection person = new MockPersonProjection(id.toString(), "John", "Doe", null);
        when(personRepository.findProjectionById(id.toString())).thenReturn(Optional.of(person));

        Optional<PersonProjection> result = personService.getPerson(id);

        assertThat(result).isEqualTo(Optional.of(person));
    }

    @Test
    void savePerson() {
        UUID id = UUID.randomUUID();
        Person person = new Person(null, "John", "Doe", null, null);
        PersonProjection mockPersonProjection = new MockPersonProjection(id.toString(), "John", "Doe", null);
        when(personRepository.saveAndReturnProjection(person)).thenReturn(mockPersonProjection);

        PersonProjection result = personService.savePerson(person);

        assertThat(result).isEqualTo(mockPersonProjection);
    }

    @Test
    void updatePersonReturnsExpectedPersonProjection() {
        UUID id = UUID.randomUUID();
        Person person = new Person(null, "John", "Doe", null, null);
        PersonProjection mockPersonProjection = new MockPersonProjection(id.toString(), "John", "Doe", null);
        when(personRepository.updateAndReturnProjection(id.toString(), person)).thenReturn(Optional.of(mockPersonProjection));

        Optional<PersonProjection> result = personService.updatePerson(id, person);

        assertThat(result).isEqualTo(Optional.of(mockPersonProjection));
    }

    @Test
    void updatePersonDoesNothingWhenPersonNotFound() {
        UUID id = UUID.randomUUID();
        Person person = new Person(id.toString(), "John", "Doe", null, null);
        when(personRepository.findById(id.toString())).thenReturn(Optional.empty());

        Optional<PersonProjection> result = personService.updatePerson(id, person);

        assertThat(result).isEmpty();
    }

    @Test
    void deletePerson() {
        UUID partnershipId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Partnership partnership = new Partnership(partnershipId.toString(), "Marriage", null, null);
        PersonProjection person = new MockPersonProjection(id.toString(), "John", "Doe", List.of(partnership));
        when(personRepository.findProjectionById(id.toString())).thenReturn(Optional.of(person));
        when(personRepository.findPersonIdsByPartnershipId(partnershipId.toString())).thenReturn(List.of(id.toString()));

        personService.deletePerson(id);

        verify(personRepository).deleteById(id.toString());
        verify(partnershipRepository).deleteById(partnershipId.toString());
    }

    static private PersonService personService;
    static private PersonRepository personRepository;
    static private PartnershipRepository partnershipRepository;

    @BeforeEach
    void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        partnershipRepository = Mockito.mock(PartnershipRepository.class);
        personService = new PersonService(personRepository, partnershipRepository);
    }
}
