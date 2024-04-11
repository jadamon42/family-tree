package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
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

    public PartnershipProjection savePartnership(Partnership partnership, List<UUID> partnerIds) {
        if (partnerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one partner ID must be provided.");
        }

        PartnershipProjection partnershipWithId = partnershipRepository.saveAndReturnProjection(partnership);
        updatePeopleInPartnership(partnershipWithId, partnerIds);
        return partnershipWithId;
    }

    public Optional<PartnershipProjection> updatePartnership(UUID partnershipId, Partnership partnership, List<UUID> partnerIds) {
        PartnershipProjection existingPartnership = partnershipRepository.findProjectionById(partnershipId.toString()).orElse(null);

        if (existingPartnership != null) {
            removePeopleNoLongerInThePartnership(partnershipId, partnerIds);
            existingPartnership = partnershipRepository.updateAndReturnProjection(partnershipId.toString(), partnership);
            updatePeopleInPartnership(existingPartnership, partnerIds);
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

    private void updatePeopleInPartnership(PartnershipProjection partnership, List<UUID> partnerIds) {
        for(UUID partnerId : partnerIds) {
            PersonProjection person = personRepository.findProjectionById(partnerId.toString())
                                                      .orElseThrow(() -> new PersonNotFoundException(partnerId));

            if (!isAlreadyInPartnership(person, partnership.getId())) {
                personRepository.addToPartnership(partnerId.toString(), partnership.getId());
            }
        }
    }

    private boolean isAlreadyInPartnership(PersonProjection person, String partnershipId) {
        return person.getPartnerships()
                     .stream()
                     .anyMatch(p -> p.getId().equals(partnershipId));
    }
}
