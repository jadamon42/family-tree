package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Collection;
import java.util.UUID;

public interface PartnershipRepository extends Neo4jRepository<Partnership, UUID> {
    @Query("""
            MATCH (c:Person {id: $childId})
            MATCH (p:Partnership {id: $partnershipId})
            CREATE (p)-[:BEGAT]->(c)
            RETURN COUNT(p) > 0
            """)
    boolean linkChildToPartnership(UUID partnershipId, UUID childId);

    @Query("""
            MATCH (p:Partnership)
            WHERE NOT (:Person)-[:PARTNER_IN]->(p)
            DETACH DELETE p
            """)
    void deleteDanglingPartnerships();

    @Query("""
        MATCH (p:Partnership)-[r]-(n)
        WHERE (p)-[:BEGAT]->(:Person {id: $childId})
        RETURN p, collect(r), collect(n)
    """)
    Collection<Partnership> findParentPartnerships(UUID childId);
}
