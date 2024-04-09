package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> getPersonById(UUID id) {
        return personRepository.findById(id.toString());
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(UUID id) {
        // TODO: can't have dangling relationships
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Optional<Person> updatePersonBaseProperties(UUID id, Person person) {
        Person existingPerson = personRepository.findById(id.toString()).orElse(null);
        if (existingPerson != null) {
            existingPerson = personRepository.save(
                    existingPerson.withFirstName(person.getFirstName())
                                             .withLastName(person.getLastName()));
        }
        return Optional.ofNullable(existingPerson);
    }
}
