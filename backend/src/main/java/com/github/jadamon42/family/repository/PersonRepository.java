package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
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
}
