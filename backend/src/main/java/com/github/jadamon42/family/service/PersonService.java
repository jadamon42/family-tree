package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.PartnershipProjection;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.github.jadamon42.family.util.PatchHelper.patch;

@Service
@Transactional
public class PersonService {
    private final PersonRepository personRepository;
    private final PartnershipRepository partnershipRepository;

    public PersonService(PersonRepository personRepository,
                         PartnershipRepository partnershipRepository) {
        this.personRepository = personRepository;
        this.partnershipRepository = partnershipRepository;
    }

    public Collection<PersonProjection> getRootPersonProjections() {
        return personRepository.findRootPeople();
    }

    public Optional<Person> getPerson(UUID id) {
        return personRepository.findById(id);
    }

    public PersonProjection createPerson(PersonRequest request) {
        return personRepository.saveAndReturnProjection(Person.fromRequest(request));
    }

    public Optional<PersonProjection> updatePerson(UUID id, PersonRequest request) {
        PersonProjection existingPerson = personRepository.findProjectionById(id).orElse(null);

        if (existingPerson != null) {
            Person person = getPatchedPerson(existingPerson, request);
            existingPerson = personRepository.updateAndReturnProjection(id, person);
        }
        return Optional.ofNullable(existingPerson);
    }

    public void deletePerson(UUID id) {
        Optional<PersonProjection> person = personRepository.findProjectionById(id);
        if (person.isPresent()) {
            deleteDanglingPartnerships(person.get());
            personRepository.deleteById(id);
        }
    }

    private void deleteDanglingPartnerships(PersonProjection person) {
        for (PartnershipProjection partnership : person.getPartnerships()) {
            Collection<UUID> partners = personRepository.findPersonIdsByPartnershipId(partnership.getId());
            // there's probably a better way to do this
            if (partners.size() == 1 && partners.stream().anyMatch(id -> id.equals(person.getId()))) {
                partnershipRepository.deleteById(partnership.getId());
            }
        }
    }

    private Person getPatchedPerson(PersonProjection existingPerson, PersonRequest request) {
        Person.PersonBuilder builder = Person.builder();
        patch(builder, Person.PersonBuilder::firstName, request.getFirstName(), existingPerson.getFirstName());
        patch(builder, Person.PersonBuilder::lastName, request.getLastName(), existingPerson.getLastName());
        patch(builder, Person.PersonBuilder::sex, request.getSex(), existingPerson.getSex());
        patch(builder, Person.PersonBuilder::birthDate, request.getBirthDate(), existingPerson.getBirthDate());
        patch(builder, Person.PersonBuilder::deathDate, request.getDeathDate(), existingPerson.getDeathDate());
        return builder.build();
    }
}
