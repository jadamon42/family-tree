//package com.github.jadamon42.family.controller;
//
//import com.github.jadamon42.family.model.Partnership;
//import com.github.jadamon42.family.service.PersonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/person/{personId}/partnership")
//public class PartnershipController {
//    private final PersonService personService;
//
//    @Autowired
//    public PartnershipController(PersonService personService) {
//        this.personService = personService;
//    }
//
//    @PostMapping
//    public Partnership addPartnership(@PathVariable UUID personId, @RequestBody Partnership partnership) {
//        return personService.addPartnership(personId, partnership);
//    }
//}
