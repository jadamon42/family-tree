package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipRequest;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataNeo4jTest
@ActiveProfiles("test")
public class PartnershipServiceTest {
    @Test
    void getPartnerships() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").ascending());
        Page<Partnership> page = partnershipService.getPartnerships(pageable);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.hasNext()).isFalse();
        assertThat(page.getContent()).isSortedAccordingTo(Comparator.comparing(Partnership::getStartDate));
    }

    @Test
    void getPartnershipsMultiPage() {
        Pageable pageable1 = PageRequest.of(0, 1, Sort.by("startDate").ascending());
        Pageable pageable2 = PageRequest.of(1, 1, Sort.by("startDate").ascending());
        Page<Partnership> page1 = partnershipService.getPartnerships(pageable1);
        Page<Partnership> page2 = partnershipService.getPartnerships(pageable2);

        assertThat(page1).isNotNull();
        assertThat(page1.getContent()).hasSize(1);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(page1.getNumber()).isEqualTo(0);
        assertThat(page1.hasNext()).isTrue();
        assertThat(page1.getContent().get(0).getId()).isEqualTo(partnershipId);

        assertThat(page2).isNotNull();
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page2.getTotalElements()).isEqualTo(2);
        assertThat(page2.getTotalPages()).isEqualTo(2);
        assertThat(page2.getNumber()).isEqualTo(1);
        assertThat(page2.hasNext()).isFalse();
        assertThat(page2.getContent().get(0).getId()).isEqualTo(unattachedPartnershipId);
    }

    @Test
    void getPartnership() {
        Partnership partnership = partnershipService.getPartnership(partnershipId).orElseThrow();

        assertThat(partnership.getId()).isEqualTo(partnershipId);
        assertThat(partnership.getType()).isEqualTo("marriage");
        assertThat(partnership.getStartDate()).isEqualTo("2021-01-01");
        assertThat(partnership.getEndDate()).isNull();
        assertThat(partnership.getPartners()).satisfies(partners -> {
            assertThat(partners).hasSize(2);
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(personInPartnershipId))).isTrue();
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(otherPersonInPartnershipId))).isTrue();
        });
        assertThat(partnership.getChildren()).satisfies(children -> {
            assertThat(children).hasSize(1);
            assertThat(children.get(0).getId()).isEqualTo(childId);
        });
    }

    @Test
    void savePartnership() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2023, 1, 1)))
                                                       .endDate(Optional.of(LocalDate.of(2023, 12, 31)))
                                                       .partnerIds(List.of(person1Id, person2Id))
                                                       .childIds(List.of(personInPartnershipId))
                                                       .build();

        Partnership savedPartnership = partnershipService.createPartnership(request);

        assertThat(savedPartnership.getId()).isNotNull();
        assertThat(savedPartnership.getType()).isEqualTo("marriage");
        assertThat(savedPartnership.getStartDate()).isEqualTo("2023-01-01");
        assertThat(savedPartnership.getEndDate()).isEqualTo("2023-12-31");
        assertThat(savedPartnership.getPartners()).satisfies(partners -> {
            assertThat(partners).hasSize(2);
            assertThat(partners.get(0).getId()).isEqualTo(person1Id);
            assertThat(partners.get(1).getId()).isEqualTo(person2Id);
        });
        assertThat(savedPartnership.getChildren()).satisfies(children -> {
            assertThat(children).hasSize(1);
            assertThat(children.get(0).getId()).isEqualTo(personInPartnershipId);
        });
    }

    @Test
    void savePartnershipFailsWhenNoPartnerIdsProvided() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2023, 1, 1)))
                                                       .endDate(Optional.of(LocalDate.of(2023, 12, 31)))
                                                       .build();

        assertThatThrownBy(() -> partnershipService.createPartnership(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one partner ID must be provided.");
    }

    @Test
    void updatePartnershipWithFields() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2023, 1, 2)))
                                                       .endDate(Optional.of(LocalDate.of(2023, 12, 31)))
                                                       .partnerIds(List.of(personInPartnershipId, otherPersonInPartnershipId))
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(partnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(partnershipId);
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isEqualTo("2023-01-02");
        assertThat(updatedPartnership.getEndDate()).isEqualTo("2023-12-31");
        assertThat(updatedPartnership.getPartners()).satisfies(partners -> {
            assertThat(partners).hasSize(2);
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(personInPartnershipId))).isTrue();
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(otherPersonInPartnershipId))).isTrue();
        });
    }

    @Test
    void updatePartnershipWithPartners() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2023, 1, 1)))
                                                       .partnerIds(List.of(person1Id, person2Id))
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(unattachedPartnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(unattachedPartnershipId);
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isEqualTo("2023-01-01");
        assertThat(updatedPartnership.getEndDate()).isNull();
        assertThat(updatedPartnership.getPartners()).satisfies(partners -> {
            assertThat(partners).hasSize(2);
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(person1Id))).isTrue();
            assertThat(partners.stream().anyMatch(person -> person.getId().equals(person2Id))).isTrue();
        });
    }

    @Test
    void updatePartnershipCorrectlyWhenOnlySpecifyingOneField() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .endDate(Optional.of(LocalDate.of(2023, 12, 31)))
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(unattachedPartnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(unattachedPartnershipId);
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isEqualTo("2022-01-01");
        assertThat(updatedPartnership.getEndDate()).isEqualTo("2023-12-31");
    }

    @Test
    void updatePartnerWithNulledField() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .startDate(Optional.empty())
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(unattachedPartnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(unattachedPartnershipId);
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isNull();
        assertThat(updatedPartnership.getEndDate()).isNull();
    }

    @Test
    void updatePartnershipWithRemovedPartner() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .partnerIds(List.of(personInPartnershipId))
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(partnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(partnershipId);
        assertThat(updatedPartnership.getPartners()).satisfies(partners -> {
            assertThat(partners).hasSize(1);
            assertThat(partners.get(0).getId()).isEqualTo(personInPartnershipId);
        });
    }

    @Test
    void updatePartnershipWithRemovedChild() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .childIds(List.of())
                                                       .build();

        Partnership updatedPartnership = partnershipService.updatePartnership(partnershipId, request).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(partnershipId);
        assertThat(updatedPartnership.getChildren()).isEmpty();
    }

    @Test
    void updatePartnershipDoesNothingWhenPartnershipNotFound() {
        PartnershipRequest request = PartnershipRequest.builder()
                                                       .type(Optional.of("marriage"))
                                                       .startDate(Optional.of(LocalDate.of(2023, 1, 1)))
                                                       .endDate(Optional.of(LocalDate.of(2023, 12, 31)))
                                                       .build();

        Optional<Partnership> partnershipProjection = partnershipService.updatePartnership(UUID.randomUUID(), request);

        assertThat(partnershipProjection).isEmpty();
    }

    @Test
    void deletePartnership() {
        partnershipService.deletePartnership(unattachedPartnershipId);
        Optional<Partnership> partnership = partnershipService.getPartnership(unattachedPartnershipId);

        assertThat(partnership).isEmpty();
    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final PartnershipService partnershipService;

    static private final UUID personInPartnershipId = UUID.randomUUID();
    static private final UUID otherPersonInPartnershipId = UUID.randomUUID();
    static private final UUID partnershipId = UUID.randomUUID();
    static private final UUID person1Id = UUID.randomUUID();
    static private final UUID person2Id = UUID.randomUUID();
    static private final UUID unattachedPartnershipId = UUID.randomUUID();
    static private final UUID childId = UUID.randomUUID();

    @Autowired
    PartnershipServiceTest(PartnershipRepository partnershipRepository, PersonRepository personRepository) {
        this.partnershipService = new PartnershipService(partnershipRepository, personRepository);
    }

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
        neo4jDriver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), AuthTokens.none());
    }

    @BeforeEach
    void setUp() {
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
            session.run(String.format("""
        CREATE (personInPartnership:Person {id: '%s', firstName:'Jonathan', lastName: 'Damon'})
        CREATE (otherPersonInPartnership:Person {id: '%s', firstName:'Hannah', lastName: 'Canady'})
        CREATE (partnership:Partnership {id: '%s', type: 'marriage', startDate: date('2021-01-01'), endDate: null})
        CREATE (personInPartnership)-[:PARTNER_IN]->(partnership),
               (otherPersonInPartnership)-[:PARTNER_IN]->(partnership)
        CREATE (person1:Person {id: '%s', firstName:'Stray1', lastName: 'Person'})
        CREATE (person2:Person {id: '%s', firstName:'Stray2', lastName: 'Person'})
        CREATE (unattachedPartnership:Partnership {id: '%s', type: 'marriage', startDate: date('2022-01-01'), endDate: null})
        CREATE (child:Person {id: '%s', firstName:'Child', lastName: 'Person'})
        CREATE (partnership)-[:BEGAT]->(child)
        """, personInPartnershipId, otherPersonInPartnershipId, partnershipId, person1Id, person2Id, unattachedPartnershipId, childId));
        }
    }

    @AfterAll
    static void stopNeo4j() {
        embeddedDatabaseServer.close();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> null);
    }
}
