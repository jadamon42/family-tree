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
class PartnershipRepositoryTest {
    @Test
    public void findAll(@Autowired PartnershipRepository partnershipRepository) {
        List<Partnership> partnerships = partnershipRepository.findAll();
        assertThat(partnerships).hasSize(3);
    }

    @Test
    public void findById(@Autowired PartnershipRepository partnershipRepository) {
        Partnership marriage = partnershipRepository.findById("10000000-0000-0000-0000-000000000000").get();
        assertThat(marriage.getType()).isEqualTo("marriage");
        assertThat(marriage.getStartDate()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(marriage.getEndDate()).isEqualTo(LocalDate.of(2021, 12, 31));
    }

    @Test
    public void save(@Autowired PartnershipRepository partnershipRepository) {
        String partnershipId = UUID.randomUUID().toString();
        Partnership partnership = new Partnership(partnershipId, "Marriage", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        partnership = partnershipRepository.save(partnership);
        assertThat(partnership.getId()).isEqualTo(partnershipId);
        assertThat(partnership.getType()).isEqualTo("Marriage");
        assertThat(partnership.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(partnership.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
    }

    @Test
    public void savePersonSavesPartnership(@Autowired PartnershipRepository partnershipRepository, @Autowired PersonRepository personRepository) {
        String partnershipId = UUID.randomUUID().toString();
        Partnership marriage = new Partnership(
                partnershipId,
                "marriage",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));
        Person person1 = new Person( null, "John", "Doe", List.of(marriage), List.of());
        Person person2 = new Person(null, "Jane", "Doe", List.of(marriage), List.of());

        person1 = personRepository.save(person1);
        person2 = personRepository.save(person2);

        Partnership marriageRead = partnershipRepository.findById(partnershipId).get();

        assertThat(marriageRead).satisfies(p -> {
            assertThat(p.getId()).isEqualTo(partnershipId);
            assertThat(p.getType()).isEqualTo("marriage");
            assertThat(p.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
            assertThat(p.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        });

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

        assertThat(personRepository.findById(person1.getId()).get().getPartnerships().get(0).getId()).
                isEqualTo(personRepository.findById(person2.getId()).get().getPartnerships().get(0).getId());

        personRepository.deleteAllById(List.of(person1.getId(), person2.getId()));

        // should be deleted by service
        marriageRead = partnershipRepository.findById(partnershipId).get();

        assertThat(marriageRead).satisfies(p -> {
            assertThat(p.getId()).isEqualTo(partnershipId);
            assertThat(p.getType()).isEqualTo("marriage");
            assertThat(p.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
            assertThat(p.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        });
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
                                 CREATE (qw:Person {id: '00000000-0000-0000-0000-000000000010', firstName:'Quinn', lastName: 'Walden'})
                                 CREATE (er:Person {id: '00000000-0000-0000-0000-000000000011', firstName:'Ethel', lastName: 'Rogers'})
                                 CREATE (ty:Person {id: '00000000-0000-0000-0000-000000000100', firstName:'Tonya', lastName: 'Yates'})
                                 CREATE (pt2:Partnership {id: '11000000-0000-0000-0000-000000000000', type: 'marriage', startDate: date('2021-01-01'), endDate: date('2021-12-31')})
                                 CREATE (pt3:Partnership {id: '11100000-0000-0000-0000-000000000000', type: 'marriage', startDate: date('2021-12-31'), endDate: date('2022-12-31')})
                                 CREATE (qw)-[:PARTNER_IN]->(pt2),
                                               (er)-[:PARTNER_IN]->(pt2),
                                               (qw)-[:PARTNER_IN]->(pt3),
                                               (ty)-[:PARTNER_IN]->(pt3),
                                               (qw)-[:PARENT_OF]->(jd)
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
