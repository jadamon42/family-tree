package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.GenealogicalLink;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomCypherQueryExecutor {
    private final Neo4jClient neo4jClient;

    public CustomCypherQueryExecutor(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Optional<GenealogicalLink> findLatestGenealogicalLink(String person1Id, String person2Id) {
        return neo4jClient.query(("""
        MATCH (commonAncestor:Person), (p1:Person {id: $person1Id}), (p2:Person {id: $person2Id})
            WHERE (((commonAncestor)-[*]->(p1) OR commonAncestor = p1)
              AND ((commonAncestor)-[*]->(p2) OR commonAncestor = p2))
        WITH p1, p2, commonAncestor
           , CASE
                WHEN commonAncestor = p1 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p1))) WHERE n:Person]) - 1
             END AS numberOfGenerationsToCommonAncestorForP1
           , CASE
                WHEN commonAncestor = p2 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p2))) WHERE n:Person]) - 1
             END AS numberOfGenerationsToCommonAncestorForP2
        ORDER BY numberOfGenerationsToCommonAncestorForP1 ASC, commonAncestor.sex DESC
        LIMIT 1
        OPTIONAL MATCH (commonAncestorsPartnership:Partnership)
            WHERE (commonAncestor)-[:PARTNER_IN]->(commonAncestorsPartnership)
              AND (commonAncestorsPartnership)-[*]->(p1)
              AND (commonAncestorsPartnership)-[*]->(p2)
        OPTIONAL MATCH (otherCommonAncestor:Person)
            WHERE (otherCommonAncestor)-[:PARTNER_IN]->(commonAncestorsPartnership)
              AND otherCommonAncestor <> commonAncestor
        RETURN p1.id AS person1Id
             , p2.id AS person2Id
             , commonAncestorsPartnership.id AS commonAncestorsPartnershipId
             , commonAncestor.id AS commonAncestorId
             , otherCommonAncestor.id AS otherCommonAncestorId
             , numberOfGenerationsToCommonAncestorForP1
             , numberOfGenerationsToCommonAncestorForP2
        """))
                          .bind(person1Id).to("person1Id")
                          .bind(person2Id).to("person2Id")
                          .fetchAs(GenealogicalLink.class)
                          .mappedBy(GenealogicalLink::fromRecord)
                          .one();
    }
}
