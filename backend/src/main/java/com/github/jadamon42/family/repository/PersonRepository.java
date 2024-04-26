package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.Person;
import com.github.jadamon42.family.model.PersonProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@GraphQlRepository
public interface PersonRepository extends Neo4jRepository<Person, UUID> {
    @Query("""
        MATCH (p:Person)
        WHERE NOT (:Partnership)-[:BEGAT]->(p)
        OPTIONAL MATCH (p)-[r]->(n)
        RETURN p, collect(r), collect(n)
    """)
    Collection<PersonProjection> findRootPeople();

    @Query("""
        MATCH (p:Person)
        WHERE NOT (:Partnership)-[:BEGAT]->(p)
        OPTIONAL MATCH (p)-[r]->(n)
        RETURN p, collect(r), collect(n)
    """)
    Collection<Person> findRootPeopleGraphQl();

    Optional<PersonProjection> findProjectionById(UUID id);

    @Query("""
            CREATE (p:Person {
                id: randomUUID(),
                firstName: :#{#person.getFirstName()},
                lastName: :#{#person.getLastName()},
                birthDate: :#{#person.getBirthDate()},
                deathDate: :#{#person.getDeathDate()},
                sex: :#{#person.getSex()}
            })
            RETURN p
            """)
    PersonProjection saveAndReturnProjection(Person person);

    @Query("""
            MATCH (p:Person {id: $id})
            OPTIONAL MATCH (p)-[r:PARTNER_IN]->(pt:Partnership)
            SET p.firstName = :#{#person.getFirstName()},
                p.lastName = :#{#person.getLastName()},
                p.birthDate = :#{#person.getBirthDate()},
                p.deathDate = :#{#person.getDeathDate()},
                p.sex = :#{#person.getSex()}
            RETURN p, collect(r), collect(pt)
            """)
    PersonProjection updateAndReturnProjection(UUID id, Person person);

    @Query("""
            MATCH (p:Person)
            WHERE (p)-[:PARTNER_IN]->(:Partnership {id: $partnershipId})
            RETURN p.id
            """)
    Collection<UUID> findPersonIdsByPartnershipId(UUID partnershipId);

    @Query("""
        MATCH (p:Person)-[r:PARTNER_IN]->(:Partnership {id: $partnershipId})
        DELETE r
    """)
    void removeAllFromPartnership(UUID partnershipId);

    @Query("""
        MATCH (p:Person {id: $personId})-[r:PARTNER_IN]->(:Partnership {id: $partnershipId})
        DELETE r
    """)
    void removeFromPartnership(UUID personId, UUID partnershipId);

    @Query("""
        MATCH (p:Person {id: $personId})
        MATCH (pt:Partnership {id: $partnershipId})
        CREATE (p)-[:PARTNER_IN]->(pt)
    """)
    void addToPartnership(UUID personId, UUID partnershipId);
}
