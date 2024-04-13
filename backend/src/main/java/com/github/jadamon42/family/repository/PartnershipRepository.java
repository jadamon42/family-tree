package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;
import java.util.UUID;

public interface PartnershipRepository extends Neo4jRepository<Partnership, UUID> {
    @Query("""
            MATCH (p:Partnership {id: $id})
            RETURN p
            """)
    Optional<PartnershipProjection> findProjectionById(UUID id);

    @Query("""
            CREATE (p:Partnership {
                id: randomUUID(),
                type: :#{#partnership.getType()},
                startDate: :#{#partnership.getStartDate()},
                endDate: :#{#partnership.getEndDate()}
            })
            RETURN p
            """)
    PartnershipProjection saveAndReturnProjection(Partnership partnership);

    @Query("""
            MATCH (p:Partnership {id: $id})
            SET p.type = :#{#partnership.getType()},
                p.startDate = :#{#partnership.getStartDate()},
                p.endDate = :#{#partnership.getEndDate()}
            RETURN p
            """)
    PartnershipProjection updateAndReturnProjection(UUID id, Partnership partnership);
}
