package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.service.PartnershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PartnershipController.class)
class PartnershipControllerTest {
    @Test
    void getPartnershipById() throws Exception {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = new Partnership(
                partnershipId.toString(),
                "marriage",
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31));

        when(partnershipService.getPartnership(partnershipId)).thenReturn(Optional.of(partnership));

        mockMvc.perform(get("/api/partnership/" + partnershipId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(partnership)));
    }

    @Test
    void addPartnership() throws Exception {
        String partnershipJson = """
        {
            "partnership": {
                "type": "marriage",
                "startDate": "2021-01-01"
            },
            "partnerIds": ["00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000001"]
        }
        """;
        UUID personId1 = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID personId2 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Partnership partnership = new Partnership(
                null,
                "marriage",
                LocalDate.of(2021, 1, 1),
                null);

        when(partnershipService.savePartnership(partnership, List.of(personId1, personId2))).thenReturn(partnership);

        mockMvc.perform(post("/api/partnership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(partnership)));
    }

    @Test
    void addPartnershipReturns400WhenNoPartnerIdsProvided() throws Exception {
        String partnershipJson = """
        {
            "partnership": {
                "type": "marriage",
                "startDate": "2021-01-01"
            },
            "partnerIds": []
        }
        """;
        Partnership partnership = new Partnership(
                null,
                "marriage",
                LocalDate.of(2021, 1, 1),
                null);

        when(partnershipService.savePartnership(partnership, List.of())).thenThrow(new IllegalArgumentException(""));

        mockMvc.perform(post("/api/partnership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchPartnership() throws Exception {
        String partnershipJson = """
        {
            "partnership": {
                "type": "marriage",
                "startDate": "2021-01-01",
                "endDate": "2021-12-31"
            },
            "partnerIds": ["00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000001"]
        }
        """;
        UUID personId1 = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID personId2 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID partnershipId = UUID.fromString("10000000-0000-0000-0000-000000000000");
        Partnership partnership = new Partnership(
                null,
                "marriage",
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31));
        Partnership partnershipWithId = partnership.withId(partnershipId.toString());

        when(partnershipService.updatePartnership(partnershipId, partnership, List.of(personId1, personId2)))
                .thenReturn(Optional.of(partnershipWithId));

        mockMvc.perform(patch("/api/partnership/" + partnershipId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(partnershipWithId)));
    }

    @Test
    void deletePartnership() throws Exception {
        mockMvc.perform(delete("/api/partnership/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PartnershipService partnershipService;
    @Autowired
    private ObjectMapper objectMapper;
}
