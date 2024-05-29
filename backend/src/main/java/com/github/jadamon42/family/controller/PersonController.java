package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.model.Relationship;
import com.github.jadamon42.family.service.GenealogicalLinkService;
import com.github.jadamon42.family.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/api/person")
@CrossOrigin(origins = "*")
public class PersonController {
    private final PersonService personService;
    private final GenealogicalLinkService genealogicalLinkService;

    @Autowired
    public PersonController(PersonService personService, GenealogicalLinkService genealogicalLinkService) {
        this.personService = personService;
        this.genealogicalLinkService = genealogicalLinkService;
    }

    @GetMapping
    public ResponseEntity<Collection<Person>> getPeople(@RequestParam Boolean rootsOnly) {
        if (! rootsOnly) {
            throw new UnsupportedOperationException("Only retrieval of root nodes is supported at this time.");
        }
        return ResponseEntity.ok(personService.getRootPeople());
    }

    @GetMapping("/relationship")
    public ResponseEntity<Relationship> getRelationship(@RequestParam UUID personFromId, @RequestParam UUID personToId) {
        return genealogicalLinkService.getRelationship(personFromId, personToId)
                                    .map(ResponseEntity::ok)
                                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getPersonById(@PathVariable UUID personId) {
        return personService.getPerson(personId)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody PersonRequest request) {
        return ResponseEntity.ok(personService.createPerson(request));
    }

    @PatchMapping("/{personId}")
    public ResponseEntity<Person> patchPerson(@PathVariable UUID personId, @RequestBody PersonRequest request) {
        return personService.updatePerson(personId, request)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID personId) {
        personService.deletePerson(personId);
        return ResponseEntity.noContent().build();
    }
}
