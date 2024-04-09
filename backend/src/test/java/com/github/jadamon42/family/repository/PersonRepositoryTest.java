package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.Person;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
public class PersonRepositoryTest {

    @Test
    public void testFindAll(@Autowired PersonRepository personRepository) {
        List<Person> people = personRepository.findAll();
        assertThat(people).hasSize(2);
    }

    @Test
    public void testFindById(@Autowired PersonRepository personRepository) {
        Person jd = personRepository.findById("00000000-0000-0000-0000-000000000000").get();
        assertThat(jd.getFirstName()).isEqualTo("Jonathan");
        assertThat(jd.getLastName()).isEqualTo("Damon");
        assertThat(jd.getPartnerships()).hasSize(1);

        Person hc = personRepository.findById("00000000-0000-0000-0000-000000000001").get();
        assertThat(hc.getFirstName()).isEqualTo("Hannah");
        assertThat(hc.getLastName()).isEqualTo("Canady");
        assertThat(hc.getPartnerships()).hasSize(1);
        assertThat(hc.getPartnerships().get(0).getId()).isEqualTo(jd.getPartnerships().get(0).getId());
    }

    @Test
    public void testSave(@Autowired PersonRepository personRepository) {
        Person person1 = new Person( null, "John", "Doe", List.of(), List.of());
        person1 = personRepository.save(person1);
        assertThat(personRepository.findAll()).hasSize(3);
        Partnership marriage = new Partnership(
                UUID.randomUUID().toString(),
                "marriage",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));
        person1 = person1.withPartnerships(List.of(marriage));
        person1 = personRepository.save(person1);
        assertThat(personRepository.findAll()).hasSize(3);
        assertThat(personRepository.findById(person1.getId()).get().getPartnerships()).hasSize(1);

        Person person2 = new Person(null, "Jane", "Doe", List.of(marriage), List.of());
        person2 = personRepository.save(person2);
        assertThat(personRepository.findAll()).hasSize(4);
        assertThat(personRepository.findById(person2.getId()).get().getPartnerships()).hasSize(1);
        assertThat(personRepository.findById(person1.getId()).get().getPartnerships().get(0).getId()).
                isEqualTo(personRepository.findById(person2.getId()).get().getPartnerships().get(0).getId());

    }

    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
            .withDisabledServer() // Don't need Neos HTTP server
            .withFixture("""
                                 CREATE (jd:Person {id: '00000000-0000-0000-0000-000000000000', firstName:'Jonathan', lastName: 'Damon'})
                                 CREATE (hc:Person {id: '00000000-0000-0000-0000-000000000001', firstName:'Hannah', lastName: 'Canady'})
                                 CREATE (pt:Partnership {id: '10000000-0000-0000-0000-000000000000', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                                 CREATE (jd)-[:PARTNER_IN]->(pt),
                                               (hc)-[:PARTNER_IN]->(pt)
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
