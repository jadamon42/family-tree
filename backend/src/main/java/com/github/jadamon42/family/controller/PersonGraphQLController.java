package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonInput;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.UUID;

@Controller
public class PersonGraphQLController {
    private final PersonRepository personRepository;

    public PersonGraphQLController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Collection<Person> getRootPeople() {
        return personRepository.findRootPeopleGraphQl();
    }

    @MutationMapping
    public Person createPerson(PersonInput personInput) {
        return personRepository.save()
    }

    @MutationMapping
    public Person updatePerson(UUID id, PersonInput personInput) {
        return null;
    }
}
