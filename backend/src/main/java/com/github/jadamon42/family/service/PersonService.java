package com.github.jadamon42.family.service;

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
public class PersonService {
    private final PersonRepository personRepository;
    private final PartnershipRepository partnershipRepository;


    public PersonService(PersonRepository personRepository, PartnershipRepository partnershipRepository) {
        this.personRepository = personRepository;
        this.partnershipRepository = partnershipRepository;
    }

    public Optional<PersonProjection> getPerson(UUID id) {
        return personRepository.findProjectionById(id.toString());
    }

    public PersonProjection savePerson(Person person) {
        return personRepository.saveAndReturnProjection(person);
    }

    public Optional<PersonProjection> updatePerson(UUID id, Person person) {
        return personRepository.updateAndReturnProjection(id.toString(), person);
    }

    public void deletePerson(UUID id) {
        Optional<PersonProjection> person = personRepository.findProjectionById(id.toString());
        if (person.isPresent()) {
            deleteDanglingPartnerships(person.get());
            personRepository.deleteById(id.toString());
        }
    }

    private void deleteDanglingPartnerships(PersonProjection person) {
        for (Partnership partnership : person.getPartnerships()) {
            List<String> partners = personRepository.findPersonIdsByPartnershipId(partnership.getId());
            // there's probably a better way to do this
            if (partners.size() == 1 && partners.get(0).equals(person.getId())) {
                partnershipRepository.deleteById(partnership.getId());
            }
        }
    }
}
