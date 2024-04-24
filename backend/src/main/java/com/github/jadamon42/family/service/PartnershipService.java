package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
import com.github.jadamon42.family.model.PartnershipRequest;
import com.github.jadamon42.family.model.PersonProjection;
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
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final PersonRepository personRepository;

    public PartnershipService(PartnershipRepository partnershipRepository, PersonRepository personRepository) {
        this.partnershipRepository = partnershipRepository;
        this.personRepository = personRepository;
    }

    public Optional<Partnership> getPartnership(UUID partnershipId) {
        return partnershipRepository.findById(partnershipId);
    }

    public PartnershipProjection createPartnership(PartnershipRequest request) {
        if (request.getPartnerIds().isEmpty()) {
            throw new IllegalArgumentException("At least one partner ID must be provided.");
        }

        Partnership partnership = Partnership.fromRequest(request);
        PartnershipProjection partnershipWithId = partnershipRepository.saveAndReturnProjection(partnership);
        updatePeopleInPartnership(partnershipWithId, request.getPartnerIds());
        return partnershipWithId;
    }

    public Optional<PartnershipProjection> updatePartnership(UUID partnershipId, PartnershipRequest request) {
        PartnershipProjection existingPartnership = partnershipRepository.findProjectionById(partnershipId).orElse(null);

        if (existingPartnership != null) {
            removePeopleNoLongerInThePartnership(partnershipId, request.getPartnerIds());
            Partnership partnership = getPatchedPartnership(existingPartnership, request);
            existingPartnership = partnershipRepository.updateAndReturnProjection(partnershipId, partnership);
            updatePeopleInPartnership(existingPartnership, request.getPartnerIds());
        }
        return Optional.ofNullable(existingPartnership);
    }

    public void deletePartnership(UUID partnershipId) {
        personRepository.removeAllFromPartnership(partnershipId);
        partnershipRepository.deleteById(partnershipId);
    }

    private void removePeopleNoLongerInThePartnership(UUID partnershipId, List<UUID> partnerIds) {
        Collection<UUID> currentPartnerIds = personRepository.findPersonIdsByPartnershipId(partnershipId);
        currentPartnerIds.stream()
                         .filter(personId -> partnershipDoesNotContainPerson(partnerIds, personId))
                         .forEach(personId -> personRepository.removeFromPartnership(personId, partnershipId));
    }

    private static boolean partnershipDoesNotContainPerson(List<UUID> partnerIds, UUID personId) {
        return !partnerIds.contains(personId);
    }

    private void updatePeopleInPartnership(PartnershipProjection partnership, List<UUID> partnerIds) {
        for(UUID partnerId : partnerIds) {
            PersonProjection person = personRepository.findProjectionById(partnerId)
                                                      .orElseThrow(() -> new PersonNotFoundException(partnerId));

            if (!isAlreadyInPartnership(person, partnership.getId())) {
                personRepository.addToPartnership(partnerId, partnership.getId());
            }
        }
    }

    private boolean isAlreadyInPartnership(PersonProjection person, UUID partnershipId) {
        return person.getPartnerships()
                     .stream()
                     .anyMatch(p -> p.getId().equals(partnershipId));
    }

    private Partnership getPatchedPartnership(PartnershipProjection existingPartnership, PartnershipRequest request) {
        Partnership.PartnershipBuilder builder = Partnership.builder();
        patch(builder,
              Partnership.PartnershipBuilder::type,
              request.getType(),
              existingPartnership.getType());
        patch(builder,
              Partnership.PartnershipBuilder::startDate,
              request.getStartDate(),
              existingPartnership.getStartDate());
        patch(builder,
              Partnership.PartnershipBuilder::endDate,
              request.getEndDate(),
              existingPartnership.getEndDate());
        return builder.build();
    }
}
