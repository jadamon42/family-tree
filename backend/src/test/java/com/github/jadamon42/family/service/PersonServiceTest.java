package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PersonServiceTest {
    private PersonService personService;
    private PersonRepository personRepository;

    @BeforeEach
    void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        personService = new PersonService(personRepository);
    }

    @Test
    void testGetPersonById() {
        UUID id = UUID.randomUUID();
        Person person = new Person(id.toString(), "John", "Doe", null, null);
        when(personRepository.findById(id.toString())).thenReturn(Optional.of(person));

        Optional<Person> result = personService.getPersonById(id);

        assertThat(result).isEqualTo(Optional.of(person));
    }

    @Test
    void testSavePerson() {
        UUID id = UUID.randomUUID();
        Person person = new Person(id.toString(), "John", "Doe", null, null);
        when(personRepository.save(person)).thenReturn(person);

        Person result = personService.savePerson(person);

        assertThat(result).isEqualTo(person);
    }

//    @Test
//    void testDeletePerson() {
//        UUID id = UUID.randomUUID();
//        personService.deletePerson(id);
//        Mockito.verify(personRepository).deleteById(id.toString());
//    }
}
