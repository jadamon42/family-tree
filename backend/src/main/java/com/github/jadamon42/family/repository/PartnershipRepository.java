package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface PartnershipRepository extends Neo4jRepository<Partnership, UUID> {
    @Query("""
            MATCH (c:Person {id: $childId})
            MATCH (p:Partnership {id: $partnershipId})
            CREATE (p)-[:BEGAT]->(c)
            RETURN COUNT(p) > 0
            """)
    boolean linkChildToPartnership(UUID partnershipId, UUID childId);
}
