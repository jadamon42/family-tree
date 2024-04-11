package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.MockPersonProjection;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
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
       PersonProjection personProjection = new MockPersonProjection(personId.toString(), "John", "Doe", null);
       when(personService.getPerson(personId)).thenReturn(Optional.of(personProjection));

       mockMvc.perform(get("/api/person/" + personId))
              .andExpect(status().isOk())
              .andExpect(content().json(objectMapper.writeValueAsString(personProjection)));
    }

    @Test
    void createPerson() throws Exception {
        String personJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";
        UUID personId = UUID.randomUUID();
        Person person = new Person(null, "John", "Doe", null, null);
        PersonProjection personProjection = new MockPersonProjection(personId.toString(), "John", "Doe", null);
        when(personService.savePerson(person)).thenReturn(personProjection);

        mockMvc.perform(post("/api/person")
               .contentType(MediaType.APPLICATION_JSON)
               .content(personJson))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(personProjection)));
    }

    @Test
    void createPersonWithIdReturns400() throws Exception {
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
    void createPersonWithPartnershipsReturns400() throws Exception {
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
    void createPersonWithChildrenReturns400() throws Exception {
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
        PersonProjection mockPersonProjection = new MockPersonProjection(personId.toString(), "John", "Doe", null);

        when(personService.updatePerson(personId, person)).thenReturn(Optional.of(mockPersonProjection));

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
        Person person = new Person(null, "John", "Doe", null, null);

        when(personService.updatePerson(personId, person)).thenReturn(Optional.empty());

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
