package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

@DataNeo4jTest
public class PersonRepositoryTest {
    @Test
    public void findById(@Autowired PersonRepository personRepository) {
        Person jd = personRepository.findById("jd").orElseThrow();
        assertThat(jd.getFirstName()).isEqualTo("Jonathan");
        assertThat(jd.getLastName()).isEqualTo("Damon");
        assertThat(jd.getPartnerships()).hasSize(1);

        Person hc = personRepository.findById("hc").orElseThrow();
        assertThat(hc.getFirstName()).isEqualTo("Hannah");
        assertThat(hc.getLastName()).isEqualTo("Canady");
        assertThat(hc.getPartnerships()).hasSize(1);
        assertThat(hc.getPartnerships().get(0).getId()).isEqualTo(jd.getPartnerships().get(0).getId());
    }

    @Test
    public void findProjectionById(@Autowired PersonRepository personRepository) {
        PersonProjection jd = personRepository.findProjectionById("jd").orElseThrow();
        assertThat(jd.getFirstName()).isEqualTo("Jonathan");
        assertThat(jd.getLastName()).isEqualTo("Damon");
        assertThat(jd.getPartnerships()).hasSize(1);
        assertThat(jd.getPartnerships().get(0).getId()).isEqualTo("jd/hc");
    }

    @Test
    public void save(@Autowired PersonRepository personRepository) {
        Partnership marriage = new Partnership(
                UUID.randomUUID().toString(),
                "marriage",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));
        Person person1 = new Person( null, "John", "Doe", List.of(marriage), List.of());
        Person person2 = new Person(null, "Jane", "Doe", List.of(marriage), List.of());

        person1 = personRepository.save(person1);
        person2 = personRepository.save(person2);

        assertThat(person1).satisfies(p -> {
            assertThat(p.getId()).isNotNull();
            assertThat(p.getFirstName()).isEqualTo("John");
            assertThat(p.getLastName()).isEqualTo("Doe");
            assertThat(p.getPartnerships()).hasSize(1);
        });

        assertThat(person2).satisfies(p -> {
            assertThat(p.getId()).isNotNull();
            assertThat(p.getFirstName()).isEqualTo("Jane");
            assertThat(p.getLastName()).isEqualTo("Doe");
            assertThat(p.getPartnerships()).hasSize(1);
        });

        assertThat(personRepository.findById(person1.getId()).orElseThrow().getPartnerships().get(0).getId()).
                isEqualTo(personRepository.findById(person2.getId()).orElseThrow().getPartnerships().get(0).getId());

        personRepository.deleteAllById(List.of(person1.getId(), person2.getId()));
    }

    @Test
    public void deletePersonDoesNotDeletePartnership(@Autowired PersonRepository personRepository, @Autowired PartnershipRepository partnershipRepository) {
        Partnership marriage = new Partnership(
                UUID.randomUUID().toString(),
                "marriage",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));
        Person person1 = new Person( null, "John", "Doe", List.of(marriage), List.of());
        Person person2 = new Person(null, "Jane", "Doe", List.of(marriage), List.of());

        person1 = personRepository.save(person1);
        person2 = personRepository.save(person2);

        personRepository.deleteAllById(List.of(person1.getId(), person2.getId()));
        partnershipRepository.deleteById(marriage.getId());
    }

    @Test
    public void update(@Autowired PersonRepository personRepository) {
        Person person = new Person(null, "John", "Doe", List.of(), List.of());

        String personId = personRepository.save(person).getId();
        PersonProjection newPerson = personRepository.updateAndReturnProjection(personId, person.withFirstName("Jon").withLastName("Doh")).orElseThrow();

        assertThat(newPerson.getFirstName()).isEqualTo("Jon");
        assertThat(newPerson.getLastName()).isEqualTo("Doh");

        personRepository.deleteById(personId);
    }

    @Test
    public void updateDoesNothingWhenPersonNotFound(@Autowired PersonRepository personRepository) {
        Person person = new Person(null, "John", "Doe", List.of(), List.of());

        Optional<PersonProjection> newPerson = personRepository.updateAndReturnProjection(UUID.randomUUID().toString(), person.withFirstName("Jon").withLastName("Doh"));

        assertThat(newPerson).isEmpty();
    }

    @Test
    void findPersonIdsByPartnershipId(@Autowired PersonRepository personRepository) {
        List<String> personIds = personRepository.findPersonIdsByPartnershipId("jd/hc");
        assertThat(personIds).containsExactlyInAnyOrder("jd", "hc");
    }

    @Test
    void removeAllFromPartnership(@Autowired PersonRepository personRepository) {
        personRepository.removeAllFromPartnership("as/df");
        List<String> personIds = personRepository.findPersonIdsByPartnershipId("as/df");
        assertThat(personIds).isEmpty();
    }

    @Test
    void removeFromPartnership(@Autowired PersonRepository personRepository) {
        personRepository.removeFromPartnership("zx", "zx/");
        List<String> personIds = personRepository.findPersonIdsByPartnershipId("zx/");
        assertThat(personIds).isEmpty();
    }

    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer() // Don't need Neos HTTP server
            .withFixture("""
                CREATE (jd:Person {id: 'jd', firstName:'Jonathan', lastName: 'Damon'})
                CREATE (hc:Person {id: 'hc', firstName:'Hannah', lastName: 'Canady'})
                CREATE (pt:Partnership {id: 'jd/hc', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                CREATE (jd)-[:PARTNER_IN]->(pt),
                       (hc)-[:PARTNER_IN]->(pt)
                CREATE (qw:Person {id: 'qw', firstName:'Quinn', lastName: 'Walden'})
                CREATE (er:Person {id: 'er', firstName:'Ethel', lastName: 'Rogers'})
                CREATE (ty:Person {id: 'ty', firstName:'Tonya', lastName: 'Yates'})
                CREATE (pt2:Partnership {id: 'qw/er', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                CREATE (pt3:Partnership {id: 'qw/ty', type: 'marriage', startDate: date('2021-12-31'), endDate: date('2022-12-31')})
                CREATE (qw)-[:PARTNER_IN]->(pt2),
                       (er)-[:PARTNER_IN]->(pt2),
                       (qw)-[:PARTNER_IN]->(pt3),
                       (ty)-[:PARTNER_IN]->(pt3),
                       (qw)-[:PARENT_OF]->(jd)
                CREATE (as:Person {id: 'as', firstName:'Alice', lastName: 'Smith'})
                CREATE (df:Person {id: 'df', firstName:'David', lastName: 'Foster'})
                CREATE (pt4:Partnership {id: 'as/df', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                CREATE (as)-[:PARTNER_IN]->(pt4),
                       (df)-[:PARTNER_IN]->(pt4)
                CREATE (zx:Person {id: 'zx', firstName:'Zelda', lastName: 'Xavier'})
                CREATE (pt5:Partnership {id: 'zx/', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                CREATE (zx)-[:PARTNER_IN]->(pt5)
                 """
            )
            .build();
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
