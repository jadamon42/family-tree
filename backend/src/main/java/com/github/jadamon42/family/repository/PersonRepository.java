package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, String> {
    @Query("""
        MATCH (p:Person)-[r]->(n)
        WHERE NOT ()-[:BEGAT]->(p)
        RETURN p, collect(r), collect(n)
    """)
    List<PersonProjection> findRootPeople();

    @Query("""
            MATCH (p:Person {id: $id})
            OPTIONAL MATCH (p)-[r:PARTNER_IN]->(pt:Partnership)
            RETURN p, collect(r), collect(pt)
            """)
    Optional<PersonProjection> findProjectionById(String id);

    @Query("""
            CREATE (p:Person {
                id: randomUUID(),
                firstName: :#{#person.getFirstName()},
                lastName: :#{#person.getLastName()}
            })
            RETURN p
            """)
    PersonProjection saveAndReturnProjection(Person person);

    @Query("""
            MATCH (p:Person {id: $id})
            OPTIONAL MATCH (p)-[r:PARTNER_IN]->(pt:Partnership)
            SET p.firstName = :#{#person.getFirstName()},
                p.lastName = :#{#person.getLastName()}
            RETURN p, collect(r), collect(pt)
            """)
    Optional<PersonProjection> updateAndReturnProjection(String id, Person person);

    @Query("""
            MATCH (p:Person)
            WHERE (p)-[:PARTNER_IN]->(:Partnership {id: $partnershipId})
            RETURN p.id
            """)
    List<String> findPersonIdsByPartnershipId(String partnershipId);

    @Query("""
        MATCH (p:Person)-[r:PARTNER_IN]->(:Partnership {id: $partnershipId})
        DELETE r
    """)
    void removeAllFromPartnership(String partnershipId);

    @Query("""
        MATCH (p:Person {id: $personId})-[r:PARTNER_IN]->(:Partnership {id: $partnershipId})
        DELETE r
    """)
    void removeFromPartnership(String personId, String partnershipId);

    @Query("""
        MATCH (p:Person {id: $personId})
        MATCH (pt:Partnership {id: $partnershipId})
        CREATE (p)-[:PARTNER_IN]->(pt)
    """)
    void addToPartnership(String personId, String partnershipId);
}
