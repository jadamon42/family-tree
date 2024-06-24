package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PartnershipNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.jadamon42.family.util.PatchHelper.patch;

@Service
@Transactional("transactionManager")
public class PersonService {
    private final PersonRepository personRepository;
    private final PartnershipRepository partnershipRepository;

    public PersonService(PersonRepository personRepository,
                         PartnershipRepository partnershipRepository) {
        this.personRepository = personRepository;
        this.partnershipRepository = partnershipRepository;
    }

    public Collection<Person> getRootPeople() {
        return personRepository.findRootPeople();
    }

    public Optional<Person> getPerson(UUID id) {
        return personRepository.findById(id);
    }

    public Person createPerson(PersonRequest request) {
        Person person = personRepository.save(Person.fromRequest(request).withId(UUID.randomUUID()));
        if (request.getParentsPartnershipId() != null && request.getParentsPartnershipId().isPresent()) {
            createParentPartnershipLink(request.getParentsPartnershipId().get(), person.getId());
            deletePlaceholdersNodes(person.getId());
        } else {
            createPlaceholderNodes(person.getId());
        }
        return person;
    }

    public Optional<Person> updatePerson(UUID id, PersonRequest request) {
        Person existingPerson = personRepository.findById(id).orElse(null);

        if (existingPerson != null) {
            Person person = getPatchedPerson(existingPerson, request);
            existingPerson = personRepository.save(person);
            if (request.getParentsPartnershipId() != null && request.getParentsPartnershipId().isPresent()) {
                createParentPartnershipLink(request.getParentsPartnershipId().get(), existingPerson.getId());
                deletePlaceholdersNodes(existingPerson.getId());
            }
        }
        return Optional.ofNullable(existingPerson);
    }

    public void deletePerson(UUID id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            deletePlaceholdersNodes(id);
            personRepository.deleteById(id);
            partnershipRepository.deleteDanglingPartnerships();
        }
    }

    private Person getPatchedPerson(Person existingPerson, PersonRequest request) {
        Person.PersonBuilder builder = Person.builder();
        builder.id(existingPerson.getId());
        patch(builder, Person.PersonBuilder::firstName, request.getFirstName(), existingPerson.getFirstName());
        patch(builder, Person.PersonBuilder::lastName, request.getLastName(), existingPerson.getLastName());
        patch(builder, Person.PersonBuilder::sex, request.getSex(), existingPerson.getSex());
        patch(builder, Person.PersonBuilder::birthDate, request.getBirthDate(), existingPerson.getBirthDate());
        patch(builder, Person.PersonBuilder::deathDate, request.getDeathDate(), existingPerson.getDeathDate());
        return builder.build();
    }

    private void createParentPartnershipLink(UUID parentsPartnershipId, UUID childId) {
        boolean linked = partnershipRepository.linkChildToPartnership(parentsPartnershipId, childId);
        if (!linked) {
            throw new PartnershipNotFoundException(parentsPartnershipId);
        }
    }

    private void createPlaceholderNodes(UUID personId) {
        Partnership placeholderPartnership = partnershipRepository.save(
                Partnership.builder()
                           .id(UUID.randomUUID())
                           .type("PLACEHOLDER")
                           .partners(
                                   List.of(
                                           Person.builder().id(UUID.randomUUID()).build(),
                                           Person.builder().id(UUID.randomUUID()).build())).build());
        createParentPartnershipLink(placeholderPartnership.getId(), personId);
    }

    private void deletePlaceholdersNodes(UUID personId) {
        Collection<Partnership> parentsPartnership = partnershipRepository.findParentPartnerships(personId);
        for (Partnership partnership : parentsPartnership) {
            if (partnership.getType().equals("PLACEHOLDER")) {
                partnershipRepository.deleteById(partnership.getId());
                for (Person partner : partnership.getPartners()) {
                    personRepository.deleteById(partner.getId());
                }
            }
        }
    }
}
