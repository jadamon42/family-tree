package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getPersonById(@PathVariable UUID personId) {
        return personService.getPerson(personId)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.savePerson(person));
    }

    @PatchMapping("/{personId}")
    public ResponseEntity<Person> patchPerson(@PathVariable UUID personId, @RequestBody Person person) {
        return personService.updatePersonBaseProperties(personId, person)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

//    @DeleteMapping("/{personId}")
//    public ResponseEntity<Void> deletePerson(@PathVariable UUID personId) {
//        personService.getPerson(personId)
//                     .ifPresent(person -> personService.deletePerson(personId));
//        return ResponseEntity.noContent().build();
//    }
}
