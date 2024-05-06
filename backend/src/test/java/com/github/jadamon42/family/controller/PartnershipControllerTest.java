package com.github.jadamon42.family.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipRequest;
import com.github.jadamon42.family.service.PartnershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PartnershipController.class)
@ActiveProfiles("test")
class PartnershipControllerTest {
    @Test
    void getPartnerships() throws Exception {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = Partnership.builder()
                                             .id(partnershipId)
                                             .type("marriage")
                                             .startDate(LocalDate.of(2021, 1, 1))
                                             .endDate(LocalDate.of(2021, 12, 31))
                                             .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").ascending());
        Page<Partnership> partnershipsPage = new PageImpl<>(List.of(partnership), pageable, 1);
        when(partnershipService.getPartnerships(pageable)).thenReturn(partnershipsPage);

        mockMvc.perform(get("/api/partnership")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "startDate,asc"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].startDate", is(partnershipsPage.getContent().get(0).getStartDate().toString())));
    }

    @Test
    void getPartnershipsReturnsEmptyPageWhenNoPartnershipsExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").ascending());
        Page<Partnership> partnershipsPage = new PageImpl<>(List.of(), pageable, 0);
        when(partnershipService.getPartnerships(pageable)).thenReturn(partnershipsPage);

        mockMvc.perform(get("/api/partnership")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "startDate,asc"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getPartnershipById() throws Exception {
        UUID partnershipId = UUID.randomUUID();
        Partnership partnership = Partnership.builder()
                                             .id(partnershipId)
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
            "type": "marriage",
            "startDate": "2021-01-01",
            "partnerIds": ["%s", "%s"]
        }
        """, personId1, personId2);
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2021, 1, 1)))
                                                       .partnerIds(List.of(personId1, personId2))
                                                       .build();
        Partnership savedPartnership = Partnership.builder()
                                                                     .id(UUID.randomUUID())
                                                                     .type("marriage")
                                                                     .startDate(LocalDate.of(2021, 1, 1))
                                                                     .build();
        when(partnershipService.createPartnership(request)).thenReturn(savedPartnership);

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
            "type": "marriage",
            "startDate": "2021-01-01"
            "partnerIds": []
        }
        """;
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2021, 1, 1)))
                                                       .build();
        when(partnershipService.createPartnership(request)).thenThrow(new IllegalArgumentException(""));

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
            "type": "marriage",
            "startDate": "2021-01-01",
            "endDate": "2021-12-31",
            "partnerIds": ["%s", "%s"]
        }
        """, personId1, personId2);
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2021, 1, 1)))
                                                       .endDate(Optional.of(LocalDate.of(2021, 12, 31)))
                                                       .partnerIds(List.of(personId1, personId2))
                                                       .build();
        Partnership savedPartnership = Partnership.builder()
                                                                          .id(partnershipId)
                                                                          .type("marriage")
                                                                          .startDate(LocalDate.of(2021, 1, 1))
                                                                          .endDate(LocalDate.of(2021, 12, 31))
                                                                          .build();
        when(partnershipService.updatePartnership(partnershipId, request))
                .thenReturn(Optional.of(savedPartnership));

        mockMvc.perform(patch("/api/partnership/" + partnershipId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partnershipJson))
                .andDo(print())
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
