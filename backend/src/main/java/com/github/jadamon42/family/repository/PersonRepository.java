package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, String> {
    @Query("""
            MATCH (p:Person)-[r]->(n)
            WHERE EXISTS(
                    (p)-[:PARTNER_IN]->(:Partnership {id: $partnershipId}))
            RETURN p, collect(r), collect(n)
            """)
    List<Person> findPeopleByPartnershipId(String partnershipId);

    @Query("""
            MATCH (p:Person {id: $person.id})
            SET p.firstName = $person.firstName,
                    p.lastName = $person.lastName
            RETURN p
            """)
    PersonProjection updatePersonProperties(Person person);

    @Query("""
        MATCH (p:Person)
        WHERE NOT (:Person)-[:PARENT_OF]->(p) 
        AND NOT EXISTS(
            (:Person)->[:PARENT_OF]->(:Person)-[:PARTNER_IN]->(:Partnership)-[:PARTNER_IN]->(p)
        )
        RETURN p
    """)
    List<Person> findPeopleWithoutParentsOrPartnersWithParents();

    @Query("""
        MATCH (p:Person)
        OPTIONAL MATCH (p)-[:PARENT_OF]->(children:Person)
        OPTIONAL MATCH (p)-[:PARTNER_IN]->(partnership:Partnership)-[:PARTNER_IN]->(partner:Person)
        WHERE NOT ()-[:PARENT_OF]->(p)
        AND NOT EXISTS(
            (p)-[:PARTNER_IN]->(:Partnership)-[:PARTNER_IN]->(:Person)-[:PARENT_OF]->()
        )
        RETURN p, collect(distinct children) as children, collect(distinct partnership) as partnerships, collect(distinct partner) as partners
        """)
    List<?> findAllPeopleWithChildrenAndPartners();
}
