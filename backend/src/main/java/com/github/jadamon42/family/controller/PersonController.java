package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Collection<PersonProjection>> getPeople(@RequestParam Boolean rootsOnly) {
        if (! rootsOnly) {
            throw new UnsupportedOperationException("Only retrieval of root nodes is supported at this time.");
        }
        return ResponseEntity.ok(personService.getRootPersonProjections());
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getPersonById(@PathVariable UUID personId) {
        return personService.getPerson(personId)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonProjection> createPerson(@RequestBody PersonRequest request) {
        return ResponseEntity.ok(personService.createPerson(request));
    }

    @PatchMapping("/{personId}")
    public ResponseEntity<PersonProjection> patchPerson(@PathVariable UUID personId, @RequestBody PersonRequest request) {
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
