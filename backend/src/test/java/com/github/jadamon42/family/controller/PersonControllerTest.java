package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {
    @Test
    void getPersonById() throws Exception {
           UUID personId = UUID.randomUUID();
           Person person = new Person(personId.toString(), "John", "Doe", null, null);

            when(personService.getPerson(personId)).thenReturn(Optional.of(person));

            mockMvc.perform(get("/api/person/" + personId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(person)));
    }

    @Test
    void createPerson() throws Exception {
            String personJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";
            UUID personId = UUID.randomUUID();
            Person person = new Person(null, "John", "Doe", null, null);
            Person personWithId = person.withId(personId.toString());

            when(personService.savePerson(person)).thenReturn(personWithId);

            mockMvc.perform(post("/api/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(personJson))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(personWithId)));
    }

    @Test
    void createPersonWithIdFails() throws Exception {
        String personJson = """
        {
            "id": "00000000-0000-0000-0000-000000000000",
            "firstName": "John",
            "lastName": "Doe"
        }
        """;

        mockMvc.perform(post("/api/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(personJson))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Unknown property: id"));
    }

    @Test
    void createPersonWithPartnershipsFails() throws Exception {
        String personJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "partnerships": [
                {
                    "type": "marriage",
                    "startDate": "2021-01-01",
                    "endDate": "2024-01-01"
                }
            ]
        }
        """;

        mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unknown property: partnerships"));
    }

    @Test
    void createPersonWithChildrenFails() throws Exception {
        String personJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "children": [
                {
                    "firstName": "Jane",
                    "lastName": "Doe"
                }
            ]
        }
        """;

        mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unknown property: children"));
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
        Person person = new Person(null, "John", "Doe", null, null);
        Person personWithId = person.withId(personId.toString());

        when(personService.updatePersonBaseProperties(personId, person)).thenReturn(Optional.of(personWithId));

        mockMvc.perform(patch("/api/person/" + personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personWithId)));
    }

    @Test
    void patchPersonFailsWhenPersonNotFound() throws Exception {
        String personJson = """
        {
            "firstName": "John",
            "lastName": "Doe"
        }
        """;
        UUID personId = UUID.randomUUID();
        Person person = new Person(null, "John", "Doe", null, null);

        when(personService.updatePersonBaseProperties(personId, person)).thenReturn(Optional.empty());

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
    @Autowired
    private ObjectMapper objectMapper;
}
