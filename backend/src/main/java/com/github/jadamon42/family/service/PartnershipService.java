package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final PersonRepository personRepository;

    public PartnershipService(PartnershipRepository partnershipRepository, PersonRepository personRepository) {
        this.partnershipRepository = partnershipRepository;
        this.personRepository = personRepository;
    }

    public Optional<Partnership> getPartnership(UUID partnershipId) {
        return partnershipRepository.findById(partnershipId.toString());
    }

    public Partnership savePartnership(Partnership partnership) {
        return partnershipRepository.save(partnership);
    }

    public Partnership savePartnership(Partnership partnership, List<UUID> partnerIds) {
        return savePartnershipAndUpdatePeople(partnership, partnerIds);
    }

    public Optional<Partnership> updatePartnership(UUID partnershipId, Partnership partnership, List<UUID> partnerIds) {
        Partnership existingPartnership = partnershipRepository.findById(partnershipId.toString()).orElse(null);

        if (existingPartnership != null) {
            removePeopleNoLongerInThePartnership(partnershipId, partnerIds);
            existingPartnership = savePartnershipAndUpdatePeople(partnership.withId(partnershipId.toString()), partnerIds);
        }
        return Optional.ofNullable(existingPartnership);
    }

    // if class a gets deleted, class b needs to be updated to no longer reference class a. class a updates class b
    // if class b gets deleted, class a needs to delete the referenced instance of class a. class b deletes class a
    // how should this be done?
    public void deletePartnership(UUID partnershipId) {
        personRepository.findPeopleByPartnershipId(partnershipId.toString())
                     .forEach(person -> personRepository.save(
                             getPersonWithPartnershipRemoved(person, partnershipId)));
        partnershipRepository.deleteById(partnershipId.toString());
    }

    private void removePeopleNoLongerInThePartnership(UUID partnershipId, List<UUID> partnerIds) {
        List<Person> currentPartners = personRepository.findPeopleByPartnershipId(partnershipId.toString());
        currentPartners.stream()
                       .filter(person -> partnershipDoesNotContainPerson(partnerIds, person))
                       .forEach(person -> personRepository.save(
                                        getPersonWithPartnershipRemoved(person, partnershipId)));
    }

    private static boolean partnershipDoesNotContainPerson(List<UUID> partnerIds, Person person) {
        return !partnerIds.contains(UUID.fromString(person.getId()));
    }

    private static Person getPersonWithPartnershipRemoved(Person person, UUID partnershipId) {
        return person.withPartnerships(
                person.getPartnerships()
                      .stream()
                      .filter(p -> !p.getId().equals(partnershipId.toString()))
                      .collect(Collectors.toList()));
    }

    private Partnership savePartnershipAndUpdatePeople(Partnership partnership, List<UUID> partnerIds) {
        Partnership partnershipWithId = partnershipRepository.save(partnership);
        for(UUID partnerId : partnerIds) {
            Optional<Person> optionalPerson = personRepository.findById(partnerId.toString());
            if (optionalPerson.isEmpty()) {
                throw new PersonNotFoundException(partnerId);
            }
            Person person = optionalPerson.get().withPartnership(partnershipWithId);
            personRepository.save(person);
        }
        return partnershipWithId;
    }
}

// partnership service shouldn't talk to person service, a partnership doesnt knw what a person is
// when a partnership is deleted, the person should be deleted in the db?

// does the person still reference the partnership if the partnership is deleted?
