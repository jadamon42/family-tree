package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
import com.github.jadamon42.family.model.PersonProjection;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataNeo4jTest
public class PartnershipServiceTest {
    @Test
    void getPartnership() {
        Partnership partnership = partnershipService.getPartnership(partnershipId).orElseThrow();

        assertThat(partnership.getId()).isEqualTo(partnershipId.toString());
        assertThat(partnership.getType()).isEqualTo("marriage");
        assertThat(partnership.getStartDate()).isEqualTo("2021-01-01");
        assertThat(partnership.getEndDate()).isNull();
    }

    @Test
    void savePartnership() {
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2023, 1, 1))
                                             .endDate(LocalDate.of(2023, 12, 31))
                                             .build();
        List<UUID> partnerIds = List.of(person1Id, person2Id);

        PartnershipProjection savedPartnership = partnershipService.savePartnership(partnership, partnerIds);
        PersonProjection person1 = personRepository.findProjectionById(person1Id.toString()).orElseThrow();
        PersonProjection person2 = personRepository.findProjectionById(person2Id.toString()).orElseThrow();

        assertThat(savedPartnership.getId()).isNotNull();
        assertThat(savedPartnership.getType()).isEqualTo("marriage");
        assertThat(savedPartnership.getStartDate()).isEqualTo("2023-01-01");
        assertThat(savedPartnership.getEndDate()).isEqualTo("2023-12-31");
        assertThat(person1.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(savedPartnership.getId());
        });
        assertThat(person2.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(savedPartnership.getId());
        });
    }

    @Test
    void savePartnershipFailsWhenNoPartnerIdsProvided() {
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2023, 1, 1))
                                             .endDate(LocalDate.of(2023, 12, 31))
                                             .build();

        assertThatThrownBy(() -> partnershipService.savePartnership(partnership, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one partner ID must be provided");
    }

    @Test
    void updatePartnershipWithFields() {
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2023, 1, 2))
                                             .endDate(LocalDate.of(2023, 12, 31))
                                             .build();
        List<UUID> partnerIds = List.of(personInPartnershipId, otherPersonInPartnershipId);

        PartnershipProjection updatedPartnership = partnershipService.updatePartnership(partnershipId, partnership, partnerIds).orElseThrow();
        PersonProjection person1 = personRepository.findProjectionById(personInPartnershipId.toString()).orElseThrow();
        PersonProjection person2 = personRepository.findProjectionById(otherPersonInPartnershipId.toString()).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(partnershipId.toString());
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isEqualTo("2023-01-02");
        assertThat(updatedPartnership.getEndDate()).isEqualTo("2023-12-31");
        assertThat(person1.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(partnershipId.toString());
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2023-01-02");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2023-12-31");
        });
        assertThat(person2.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(partnershipId.toString());
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2023-01-02");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2023-12-31");
        });
    }

    @Test
    void updatePartnershipWithPartners() {
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2023, 1, 1))
                                             .build();
        List<UUID> partnerIds = List.of(person1Id, person2Id);

        PartnershipProjection updatedPartnership = partnershipService.updatePartnership(unattachedPartnershipId, partnership, partnerIds).orElseThrow();
        PersonProjection person1 = personRepository.findProjectionById(person1Id.toString()).orElseThrow();
        PersonProjection person2 = personRepository.findProjectionById(person2Id.toString()).orElseThrow();

        assertThat(updatedPartnership.getId()).isEqualTo(unattachedPartnershipId.toString());
        assertThat(updatedPartnership.getType()).isEqualTo("marriage");
        assertThat(updatedPartnership.getStartDate()).isEqualTo("2023-01-01");
        assertThat(updatedPartnership.getEndDate()).isNull();
        assertThat(person1.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(unattachedPartnershipId.toString());
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2023-01-01");
            assertThat(partnerships.get(0).getEndDate()).isNull();
        });
        assertThat(person2.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getId()).isEqualTo(unattachedPartnershipId.toString());
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2023-01-01");
            assertThat(partnerships.get(0).getEndDate()).isNull();
        });
    }

    @Test
    void updatePartnershipCorrectlyWhenOnlySpecifyingOneField() {
        Partnership partnership = Partnership.builder()
                                             .endDate(LocalDate.of(2023, 12, 31))
                                             .build();

        // TODO
    }

    @Test
    void updatePartnershipDoesNothingWhenPartnershipNotFound() {
        Partnership partnership = Partnership.builder()
                                             .type("marriage")
                                             .startDate(LocalDate.of(2023, 1, 1))
                                             .endDate(LocalDate.of(2023, 12, 31))
                                             .build();

        Optional<PartnershipProjection> partnershipProjection = partnershipService.updatePartnership(UUID.randomUUID(), partnership, List.of());

        assertThat(partnershipProjection).isEmpty();
    }

    @Test
    void deletePartnership() {
        partnershipService.deletePartnership(unattachedPartnershipId);
        Optional<Partnership> partnership = partnershipService.getPartnership(unattachedPartnershipId);

        assertThat(partnership).isEmpty();
    }

    @Test
    void deletePartnershipUpdatesPartners() {
        partnershipService.deletePartnership(partnershipId);
        PersonProjection person1 = personRepository.findProjectionById(personInPartnershipId.toString()).orElseThrow();
        PersonProjection person2 = personRepository.findProjectionById(otherPersonInPartnershipId.toString()).orElseThrow();

        assertThat(person1.getPartnerships()).isEmpty();
        assertThat(person2.getPartnerships()).isEmpty();
    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final PartnershipService partnershipService;
    private final PersonRepository personRepository;

    static private final UUID personInPartnershipId = UUID.randomUUID();
    static private final UUID otherPersonInPartnershipId = UUID.randomUUID();
    static private final UUID partnershipId = UUID.randomUUID();
    static private final UUID person1Id = UUID.randomUUID();
    static private final UUID person2Id = UUID.randomUUID();
    static private final UUID unattachedPartnershipId = UUID.randomUUID();

    @Autowired
    PartnershipServiceTest(PartnershipRepository partnershipRepository, PersonRepository personRepository) {
        this.personRepository = personRepository;
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
        CREATE (unattachedPartnership:Partnership {id: '%s', type: 'marriage', startDate: date('2021-01-01'), endDate: null})
        """, personInPartnershipId, otherPersonInPartnershipId, partnershipId, person1Id, person2Id, unattachedPartnershipId));
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
