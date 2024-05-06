package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Collection;
import java.util.UUID;

public interface PersonRepository extends Neo4jRepository<Person, UUID> {
    @Query("""
        MATCH (p:Person)
        WHERE NOT (:Partnership)-[:BEGAT]->(p)
        RETURN p
    """)
    Collection<Person> findRootPeople();
}
