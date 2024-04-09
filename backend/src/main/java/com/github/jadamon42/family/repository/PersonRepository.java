package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonRepository extends Neo4jRepository<Person, String> {
}
