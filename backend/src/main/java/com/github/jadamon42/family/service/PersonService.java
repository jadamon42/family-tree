package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PersonService {
    private final PersonRepository personRepository;
    private final PartnershipRepository partnershipRepository;


    public PersonService(PersonRepository personRepository, PartnershipRepository partnershipRepository) {
        this.personRepository = personRepository;
        this.partnershipRepository = partnershipRepository;
    }

    public Optional<Person> getPerson(UUID id) {
        return personRepository.findById(id.toString());
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> updatePerson(UUID id, Person person) {
        Person existingPerson = personRepository.findById(id.toString()).orElse(null);
        if (existingPerson != null) {
            existingPerson = personRepository.save(
                    existingPerson.withFirstName(person.getFirstName())
                                  .withLastName(person.getLastName()));
        }
        return Optional.ofNullable(existingPerson);
    }

    public void deletePerson(UUID id) {
        Optional<Person> person = personRepository.findById(id.toString());
        if (person.isPresent()) {
            deleteDanglingPartnerships(person.get());
            personRepository.deleteById(id.toString());
        }
    }

    private void deleteDanglingPartnerships(Person person) {
        for (Partnership partnership : person.getPartnerships()) {
            List<Person> partners = personRepository.findPeopleByPartnershipId(partnership.getId());
            // theres a more efficient way to do this
            if (partners.size() == 1) {
                partnershipRepository.deleteById(partnership.getId());
            }
        }
    }
}
