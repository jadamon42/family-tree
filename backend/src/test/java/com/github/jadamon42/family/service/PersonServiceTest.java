package com.github.jadamon42.family.service;

import com.github.jadamon42.family.exception.PartnershipNotFoundException;
import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import com.github.jadamon42.family.model.PersonRequest;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataNeo4jTest
@ActiveProfiles("test")
@Import(CustomCypherQueryExecutor.class)
public class PersonServiceTest {
    @Test
    void getRootPersonProjections() {
        Collection<PersonProjection> people = personService.getRootPersonProjections();

        PersonProjection personInPartnership = people.stream()
                                                     .filter(p -> p.getId().equals(personInPartnershipId))
                                                     .findFirst()
                                                     .orElseThrow();
        PersonProjection otherPersonInPartnership = people.stream()
                                                          .filter(p -> p.getId().equals(otherPersonInPartnershipId))
                                                          .findFirst()
                                                          .orElseThrow();
        PersonProjection person = people.stream()
                                        .filter(p -> p.getId().equals(personId))
                                        .findFirst()
                                        .orElseThrow();
        PersonProjection personInDanglingPartnership = people.stream()
                                                             .filter(p -> p.getId().equals(personInDanglingPartnershipId))
                                                             .findFirst()
                                                             .orElseThrow();
        PersonProjection spouseOfChild1 = people.stream()
                                                .filter(p -> p.getFirstName().equals("Spouse"))
                                                .findFirst()
                                                .orElseThrow();


        assertThat(people).hasSize(5);
        assertThat(personInPartnership).satisfies(p -> {
            assertThat(p.getFirstName()).isEqualTo("Jonathan");
            assertThat(p.getLastName()).isEqualTo("Damon");
        });
        assertThat(otherPersonInPartnership).satisfies(p -> {
            assertThat(p.getFirstName()).isEqualTo("Hannah");
            assertThat(p.getLastName()).isEqualTo("Canady");
        });
        assertThat(person).satisfies(p -> {
            assertThat(p.getFirstName()).isEqualTo("Stray");
            assertThat(p.getLastName()).isEqualTo("Person");
        });
        assertThat(personInDanglingPartnership).satisfies(p -> {
            assertThat(p.getFirstName()).isEqualTo("Stray");
            assertThat(p.getLastName()).isEqualTo("Person");
        });
        assertThat(spouseOfChild1).satisfies(p -> {
            assertThat(p.getFirstName()).isEqualTo("Spouse");
            assertThat(p.getLastName()).isEqualTo("Of Child One");
            assertThat(p.getPartnerships().get(0)).satisfies(partnership -> {
                assertThat(partnership.getType()).isEqualTo("marriage");
                assertThat(partnership.getStartDate()).isEqualTo("2021-01-01");
                assertThat(partnership.getEndDate()).isEqualTo("2021-12-31");
            });
        });
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
    void savePersonWithParentPartnership() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .parentsPartnershipId(Optional.of(partnershipId))
                                             .build();

        PersonProjection savedPerson = personService.createPerson(request);
        Partnership partnership = partnershipRepository.findById(partnershipId).orElseThrow();

        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getFirstName()).isEqualTo("John");
        assertThat(savedPerson.getLastName()).isEqualTo("Doe");
        assertThat(savedPerson.getPartnerships()).isEmpty();
        assertThat(partnership.getChildren()).hasSize(3);
    }

    @Test
    void savePersonWithParentPartnershipThrowsExceptionWhenPartnershipNotFound() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .parentsPartnershipId(Optional.of(UUID.randomUUID()))
                                             .build();

        assertThatThrownBy(() -> personService.createPerson(request))
            .isInstanceOf(PartnershipNotFoundException.class);
    }

    @Test
    void updatePerson() {
        PersonRequest request = PersonRequest.builder()
                                             .firstName(Optional.of("John"))
                                             .lastName(Optional.of("Doe"))
                                             .build();

        PersonProjection updatedPerson = personService.updatePerson(personInPartnershipId, request).orElseThrow();

        assertThat(updatedPerson.getId()).isEqualTo(personInPartnershipId);
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
        Partnership partnership = partnershipRepository.findById(partnershipId).orElseThrow();

        assertThat(updatedPerson.getId()).isEqualTo(personInPartnershipId);
        assertThat(updatedPerson.getFirstName()).isEqualTo("Jon");
        assertThat(updatedPerson.getLastName()).isEqualTo("Damon");
        assertThat(updatedPerson.getPartnerships()).satisfies(partnerships -> {
            assertThat(partnerships).hasSize(1);
            assertThat(partnerships.get(0).getType()).isEqualTo("marriage");
            assertThat(partnerships.get(0).getStartDate()).isEqualTo("2021-01-01");
            assertThat(partnerships.get(0).getEndDate()).isEqualTo("2021-12-31");
            assertThat(partnerships.get(0).getId()).isEqualTo(partnership.getId());
        });
        // No changes to the fields not specified in the projection
        assertThat(partnership.getChildren()).hasSize(2);
    }

    @Test
    void updatePersonWithParentPartnership() {
        PersonRequest request = PersonRequest.builder()
                                             .lastName(Optional.of("Doe"))
                                             .parentsPartnershipId(Optional.of(partnershipId))
                                             .build();

        PersonProjection updatedPerson = personService.updatePerson(personId, request).orElseThrow();
        Partnership partnership = partnershipRepository.findById(partnershipId).orElseThrow();

        assertThat(updatedPerson.getId()).isEqualTo(personId);
        assertThat(updatedPerson.getFirstName()).isEqualTo("Stray");
        assertThat(updatedPerson.getLastName()).isEqualTo("Doe");
        assertThat(updatedPerson.getPartnerships()).isEmpty();
        assertThat(partnership.getChildren()).hasSize(3);
    }

    @Test
    void updatePersonWithParentPartnershipThrowsExceptionWhenPartnershipNotFound() {
        PersonRequest request = PersonRequest.builder()
                                             .lastName(Optional.of("Doe"))
                                             .parentsPartnershipId(Optional.of(UUID.randomUUID()))
                                             .build();

        assertThatThrownBy(() -> personService.updatePerson(personId, request))
            .isInstanceOf(PartnershipNotFoundException.class);
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
        assertThat(partnershipRepository.findById(danglingPartnershipId)).isEmpty();
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
    static private final UUID distantRelative1Id = UUID.randomUUID();
    static private final UUID distantRelative2Id = UUID.randomUUID();

    @Autowired
    PersonServiceTest(PersonRepository personRepository,
                      PartnershipRepository partnershipRepository) {
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
        
        // Children
        CREATE (child1:Person {id: randomUuid(), firstName:'Child', lastName: 'One'})
        CREATE (child2:Person {id: randomUuid(), firstName:'Child', lastName: 'Two'})
        CREATE (partnership)-[:BEGAT]->(child1),
               (partnership)-[:BEGAT]->(child2)
        CREATE (spouseOfChild1:Person {id: randomUuid(), firstName:'Spouse', lastName: 'Of Child One'})
        CREATE (childPartnership1:Partnership {id: randomUuid(), type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
        CREATE (childPartnership2:Partnership {id: randomUuid(), type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
        CREATE (child1)-[:PARTNER_IN]->(childPartnership1),
               (spouseOfChild1)-[:PARTNER_IN]->(childPartnership1),
               (child2)-[:PARTNER_IN]->(childPartnership2)
        CREATE (grandchild1:Person {id: '%s', firstName:'Grandchild', lastName: 'One'})
        CREATE (grandchild2:Person {id: '%s', firstName:'Grandchild', lastName: 'Two'})
        CREATE (childPartnership1)-[:BEGAT]->(grandchild1),
               (childPartnership2)-[:BEGAT]->(grandchild2)
        """, personInPartnershipId, otherPersonInPartnershipId, partnershipId, personId, personInDanglingPartnershipId, danglingPartnershipId, distantRelative1Id, distantRelative2Id));
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
