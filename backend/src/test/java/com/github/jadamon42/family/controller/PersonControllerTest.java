package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.model.Relationship;
import com.github.jadamon42.family.model.Sex;
import com.github.jadamon42.family.service.GenealogicalLinkService;
import com.github.jadamon42.family.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@ActiveProfiles("test")
class PersonControllerTest {
    @Test
    void getPeopleRootsOnly() throws Exception {
        mockMvc.perform(get("/api/person?rootsOnly=true"))
               .andExpect(status().isOk())
               .andExpect(content().json("[]"));
    }

    @Test
    void getRelationship() throws Exception {
        UUID personFromId = UUID.randomUUID();
        UUID personToId = UUID.randomUUID();
        Relationship relationship = new Relationship(Set.of(personFromId, personToId), "A Relationship", "An Inverse Relationship");
        when(genealogicalLinkService.getRelationship(personFromId, personToId)).thenReturn(Optional.of(relationship));

        mockMvc.perform(get("/api/person/relationship?personFromId=" + personFromId + "&personToId=" + personToId))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(relationship)));
    }

    @Test
    void getRelationshipReturns404WhenNotFound() throws Exception {
        UUID personFromId = UUID.randomUUID();
        UUID personToId = UUID.randomUUID();
        when(genealogicalLinkService.getRelationship(personFromId, personToId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/person/relationship?personFromId=" + personFromId + "&personToId=" + personToId))
               .andExpect(status().isNotFound());
    }

    @Test
    void getRelationshipReturns400WhenNoParams() throws Exception {
        mockMvc.perform(get("/api/person"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void getPersonById() throws Exception {
       UUID personId = UUID.randomUUID();
       Person person = Person.builder()
                             .id(personId)
                             .firstName("John")
                             .lastName("Doe")
                             .build();
       when(personService.getPerson(personId)).thenReturn(Optional.of(person));

       mockMvc.perform(get("/api/person/" + personId))
              .andExpect(status().isOk())
              .andExpect(content().json(objectMapper.writeValueAsString(person)));
    }

    @Test
    void createPerson() throws Exception {
        String personJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";
        UUID personId = UUID.randomUUID();
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();
        Person savedPerson = Person.builder()
                                                           .id(personId)
                                                           .firstName("John")
                                                           .lastName("Doe")
                                                           .build();
        when(personService.createPerson(request)).thenReturn(savedPerson);

        mockMvc.perform(post("/api/person")
               .contentType(MediaType.APPLICATION_JSON)
               .content(personJson))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(savedPerson)));
    }

    @Test
    void createPersonWithAllFields() throws Exception {
        String personJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "2000-01-01",
                    "deathDate": "2020-01-01",
                    "sex": "male"
                }
                """;
        UUID personId = UUID.randomUUID();
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .birthDate(Optional.of(LocalDate.of(2000, 1, 1)))
                                             .deathDate(Optional.of(LocalDate.of(2020, 1, 1)))
                                             .sex(Optional.of(Sex.MALE))
                                             .build();
        Person savedPerson = Person.builder()
                                                           .id(personId)
                                                           .firstName("John")
                                                           .lastName("Doe")
                                                           .birthDate(LocalDate.of(2000, 1, 1))
                                                           .deathDate(LocalDate.of(2020, 1, 1))
                                                           .sex(Sex.MALE)
                                                           .build();
        when(personService.createPerson(request)).thenReturn(savedPerson);

        mockMvc.perform(post("/api/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(personJson))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(savedPerson)));
    }

    @Test
    void patchPerson() throws Exception {
        String personJson = """
        {
            "firstName": "John",
            "lastName": "Doe"
        }
        """;
        UUID personId = UUID.randomUUID();
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();
        Person mockPersonProjection = Person.builder()
                                                                    .id(personId)
                                                                    .firstName("John")
                                                                    .lastName("Doe")
                                                                    .build();
        when(personService.updatePerson(personId, request)).thenReturn(Optional.of(mockPersonProjection));

        mockMvc.perform(patch("/api/person/" + personId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(personJson))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(mockPersonProjection)));
    }

    @Test
    void patchPersonReturns404WhenPersonNotFound() throws Exception {
        String personJson = """
        {
            "firstName": "John",
            "lastName": "Doe"
        }
        """;
        UUID personId = UUID.randomUUID();
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();
        when(personService.updatePerson(personId, request)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/person/" + personId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(personJson))
               .andExpect(status().isNotFound());
    }

    @Test
    void deletePerson() throws Exception {
        mockMvc.perform(delete("/api/person/" + UUID.randomUUID()))
               .andExpect(status().isNoContent());
    }

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonService personService;
    @MockBean
    private GenealogicalLinkService genealogicalLinkService;
    @Autowired
    private ObjectMapper objectMapper;
}
