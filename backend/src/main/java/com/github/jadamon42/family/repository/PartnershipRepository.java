package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PartnershipRepository extends Neo4jRepository<Partnership, String> {
}
