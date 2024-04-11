package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Partnership savePartnership(Partnership partnership, List<UUID> partnerIds) {
        if (partnerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one partner must be provided.");
        }
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

    public void deletePartnership(UUID partnershipId) {
        personRepository.removeAllFromPartnership(partnershipId.toString());
        partnershipRepository.deleteById(partnershipId.toString());
    }

    private void removePeopleNoLongerInThePartnership(UUID partnershipId, List<UUID> partnerIds) {
        List<String> currentPartnerIds = personRepository.findPersonIdsByPartnershipId(partnershipId.toString());
        currentPartnerIds.stream()
                         .filter(personId -> partnershipDoesNotContainPerson(partnerIds, personId))
                         .forEach(personId -> personRepository.removeFromPartnership(personId, partnershipId.toString()));
    }

    private static boolean partnershipDoesNotContainPerson(List<UUID> partnerIds, String personId) {
        return !partnerIds.contains(UUID.fromString(personId));
    }

    private Partnership savePartnershipAndUpdatePeople(Partnership partnership, List<UUID> partnerIds) {
        Partnership partnershipWithId = partnershipRepository.save(partnership);
        for(UUID partnerId : partnerIds) {
            Optional<PersonProjection> optionalPerson = personRepository.findProjectionById(partnerId.toString());
            if (optionalPerson.isEmpty()) {
                throw new PersonNotFoundException(partnerId);
            }
            Person person = Person.fromProjection(optionalPerson.get()).withPartnership(partnershipWithId);
            personRepository.updateAndReturnProjection(person.getId(), person);
        }
        return partnershipWithId;
    }
}
