package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.MockPartnershipProjection;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
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
        Partnership partnership = Partnership.builder()
                                             .id(partnershipId.toString())
                                             .type("marriage")
                                             .startDate(LocalDate.of(2021, 1, 1))
                                             .endDate(LocalDate.of(2021, 12, 31))
                                             .build();
        when(partnershipService.getPartnership(partnershipId)).thenReturn(Optional.of(partnership));

        mockMvc.perform(get("/api/partnership/" + partnershipId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(partnership)));
    }

    @Test
    void addPartnership() throws Exception {
        UUID personId1 = UUID.randomUUID();
        UUID personId2 = UUID.randomUUID();

        String partnershipJson = String.format("""
        {
            "partnership": {
                "type": "marriage",
                "startDate": "2021-01-01"
            },
            "partnerIds": ["%s", "%s"]
        }
        """, personId1, personId2);
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2021, 1, 1))
                                             .build();
        PartnershipProjection savedPartnership = MockPartnershipProjection.builder()
                                                                     .id(UUID.randomUUID().toString())
                                                                     .type("marriage")
                                                                     .startDate(LocalDate.of(2021, 1, 1))
                                                                     .build();
        when(partnershipService.savePartnership(partnership, List.of(personId1, personId2))).thenReturn(savedPartnership);

        mockMvc.perform(post("/api/partnership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedPartnership)));
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
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2021, 1, 1))
                                             .build();
        when(partnershipService.savePartnership(partnership, List.of())).thenThrow(new IllegalArgumentException(""));

        mockMvc.perform(post("/api/partnership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchPartnership() throws Exception {
        UUID personId1 = UUID.randomUUID();
        UUID personId2 = UUID.randomUUID();
        UUID partnershipId = UUID.randomUUID();
        String partnershipJson = String.format("""
        {
            "partnership": {
                "type": "marriage",
                "startDate": "2021-01-01",
                "endDate": "2021-12-31"
            },
            "partnerIds": ["%s", "%s"]
        }
        """, personId1, personId2);
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2021, 1, 1))
                                             .endDate(LocalDate.of(2021, 12, 31))
                                             .build();
        PartnershipProjection savedPartnership = MockPartnershipProjection.builder()
                                                                           .id(partnershipId.toString())
                                                                           .type("marriage")
                                                                           .startDate(LocalDate.of(2021, 1, 1))
                                                                           .endDate(LocalDate.of(2021, 12, 31))
                                                                           .build();
        when(partnershipService.updatePartnership(partnershipId, partnership, List.of(personId1, personId2)))
                .thenReturn(Optional.of(savedPartnership));

        mockMvc.perform(patch("/api/partnership/" + partnershipId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedPartnership)));
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
