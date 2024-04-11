package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.model.PersonRequest;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
public class PersonServiceTest {
    @Test
    void getRootPersonProjections() {

    }

    @Test
    void getPerson() {
        Person person = personService.getPerson(personInPartnershipId).orElseThrow();
        assertThat(person.getFirstName()).isEqualTo("Jonathan");
        assertThat(person.getLastName()).isEqualTo("Damon");
        assertThat(person.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2021-01-01");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2021-12-31");
        });
    }

    @Test
    void savePerson() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();

        PersonProjection savedPerson = personService.createPerson(request);

        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getFirstName()).isEqualTo("John");
        assertThat(savedPerson.getLastName()).isEqualTo("Doe");
    }

    @Test
    void updatePerson() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();

        PersonProjection updatedPerson = personService.updatePerson(personInPartnershipId, request).orElseThrow();

        assertThat(updatedPerson.getId()).isEqualTo(personInPartnershipId.toString());
        assertThat(updatedPerson.getFirstName()).isEqualTo("John");
        assertThat(updatedPerson.getLastName()).isEqualTo("Doe");
        assertThat(updatedPerson.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2021-01-01");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2021-12-31");
        });
    }

    @Test
    void updatePersonDoesNothingWhenPersonNotFound() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();

        Optional<PersonProjection> person = personService.updatePerson(UUID.randomUUID(), request);

        assertThat(person).isEmpty();
    }

    @Test
    void updatePersonCorrectlyWhenOnlySpecifyingOneField() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("Jon"))
                                             .build();

        PersonProjection updatedPerson = personService.updatePerson(personInPartnershipId, request).orElseThrow();

        assertThat(updatedPerson.getId()).isEqualTo(personInPartnershipId.toString());
        assertThat(updatedPerson.getFirstName()).isEqualTo("Jon");
        assertThat(updatedPerson.getLastName()).isEqualTo("Damon");
        assertThat(updatedPerson.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2021-01-01");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2021-12-31");
        });
    }

    @Test
    void deletePerson() {
        personService.deletePerson(personId);

        assertThat(personService.getPerson(personId)).isEmpty();
    }

    @Test
    void deletePersonDeletesDanglingPartnerships() {
        personService.deletePerson(personInDanglingPartnershipId);

        assertThat(personService.getPerson(personInDanglingPartnershipId)).isEmpty();
        assertThat(partnershipRepository.findById(danglingPartnershipId.toString())).isEmpty();
    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final PersonService personService;
    private final PartnershipRepository partnershipRepository;

    static private final UUID personInPartnershipId = UUID.randomUUID();
    static private final UUID otherPersonInPartnershipId = UUID.randomUUID();
    static private final UUID partnershipId = UUID.randomUUID();
    static private final UUID personId = UUID.randomUUID();
    static private final UUID personInDanglingPartnershipId = UUID.randomUUID();
    static private final UUID danglingPartnershipId = UUID.randomUUID();

    @Autowired
    PersonServiceTest(PersonRepository personRepository, PartnershipRepository partnershipRepository) {
        this.partnershipRepository = partnershipRepository;
        this.personService = new PersonService(personRepository, partnershipRepository);
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
        CREATE (partnership:Partnership {id: '%s', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
        CREATE (personInPartnership)-[:PARTNER_IN]->(partnership),
               (otherPersonInPartnership)-[:PARTNER_IN]->(partnership)
        CREATE (person:Person {id: '%s', firstName:'Stray', lastName: 'Person'})
        CREATE (personInDanglingPartnership:Person {id: '%s', firstName:'Stray', lastName: 'Person'})
        CREATE (danglingPartnership:Partnership {id: '%s', type: 'marriage', startDate: date('2021-12-31'), endDate: null})
        CREATE (personInDanglingPartnership)-[:PARTNER_IN]->(danglingPartnership)
        """, personInPartnershipId, otherPersonInPartnershipId, partnershipId, personId, personInDanglingPartnershipId, danglingPartnershipId));
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
