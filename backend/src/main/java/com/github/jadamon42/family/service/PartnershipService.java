package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipRequest;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public Partnership createPartnership(PartnershipRequest request) {
        if (request.getPartnerIds().isEmpty()) {
            throw new IllegalArgumentException("At least one partner ID must be provided.");
        }

        Partnership partnership = Partnership.fromRequest(request).withId(UUID.randomUUID());
        partnership = updatePartners(partnership, request.getPartnerIds());
        partnership = updateChildren(partnership, request.getChildIds());
        partnership = partnershipRepository.save(partnership);
        return partnership;
    }

    public Optional<Partnership> updatePartnership(UUID partnershipId, PartnershipRequest request) {
        Partnership existingPartnership = partnershipRepository.findById(partnershipId).orElse(null);
        Partnership updatedPartnership = null;

        if (existingPartnership != null) {
            updatedPartnership = getPatchedPartnership(existingPartnership, request);
            updatedPartnership = updatePartners(updatedPartnership, request.getPartnerIds());
            updatedPartnership = updateChildren(updatedPartnership, request.getChildIds());
            updatedPartnership = partnershipRepository.save(updatedPartnership);
        }
        return Optional.ofNullable(updatedPartnership);
    }

    public void deletePartnership(UUID partnershipId) {
        partnershipRepository.deleteById(partnershipId);
    }

    private Partnership updatePartners(Partnership partnership, List<UUID> partnerIds) {
        if (!partnership.getPartners().isEmpty()) {
            List<Person> newPartners = partnership.getPartners().stream().filter(person -> partnerIds.contains(person.getId())).toList();
            partnership = partnership.withPartners(newPartners);
        }
        for(UUID partnerId : partnerIds) {
            if (!isAlreadyInPartnership(partnership, partnerId)) {
                Person partner = personRepository.findById(partnerId).orElseThrow();
                List<Person> newPartners = new ArrayList<>(partnership.getPartners().stream().toList());
                newPartners.add(partner);
                partnership = partnership.withPartners(newPartners);
            }
        }
        return partnership;
    }

    private Partnership updateChildren(Partnership partnership, List<UUID> childIds) {
        if (!partnership.getChildren().isEmpty()) {
            List<Person> newChildren = partnership.getChildren().stream().filter(person -> childIds.contains(person.getId())).toList();
            partnership = partnership.withChildren(newChildren);
        }
        for(UUID childId : childIds) {
            Person child = personRepository.findById(childId).orElseThrow();
            List<Person> newChildren = new ArrayList<>(partnership.getChildren().stream().toList());
            newChildren.add(child);
            partnership = partnership.withChildren(newChildren);
        }
        return partnership;
    }

    private boolean isAlreadyInPartnership(Partnership partnership, UUID partnerId) {
        return partnership.getPartners().stream().anyMatch(person -> person.getId().equals(partnerId));
    }

    private Partnership getPatchedPartnership(Partnership existingPartnership, PartnershipRequest request) {
        Partnership.PartnershipBuilder builder = Partnership.builder();
        builder.id(existingPartnership.getId());
        builder.partners(existingPartnership.getPartners());
        builder.children(existingPartnership.getChildren());
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
