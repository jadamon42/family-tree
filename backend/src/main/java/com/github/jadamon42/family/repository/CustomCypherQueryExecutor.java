package com.github.jadamon42.family.repository;

import com.github.jadamon42.family.model.GenealogicalLink;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomCypherQueryExecutor {
    private final Neo4jClient neo4jClient;

    public CustomCypherQueryExecutor(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Optional<GenealogicalLink> findLatestGenealogicalLink(UUID person1Id, UUID person2Id) {
        return neo4jClient.query(("""
        MATCH (p1:Person {id: $person1Id})
        WHERE p1.id = $person2Id
        RETURN p1.id AS person1Id
             , p1.id AS person2Id
             , p1.sex AS person1Sex
             , p1.sex AS person2Sex
             , false AS person1MarriedIn
             , false AS person2MarriedIn
             , p1.id AS commonAncestorId
             , null AS otherCommonAncestorId
             , null AS sharedAncestralPartnershipId
             , 0 AS person1NumberOfPersonNodesToCommonAncestor
             , 0 AS person2NumberOfPersonNodesToCommonAncestor
             , [p1.id] AS pathIdsFromCommonAncestorToPerson1
             , [p1.id] AS pathIdsFromCommonAncestorToPerson2
    UNION
        MATCH (p1:Person {id: $person1Id})-[*]-(p2:Person {id: $person2Id})
        OPTIONAL MATCH (directCommonAncestor:Person)
            WHERE ((directCommonAncestor)-[*]->(p1) OR directCommonAncestor = p1)
              AND ((directCommonAncestor)-[*]->(p2) OR directCommonAncestor = p2)
        OPTIONAL MATCH (p1CommonAncestorThroughMarriage:Person)
            WHERE ((p1CommonAncestorThroughMarriage)-[*]->(p1) OR p1CommonAncestorThroughMarriage = p1)
              AND ((p1CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p2)
                    OR (p1CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(:Person)-[:PARTNER_IN]->(:Partnership)-[:BEGAT]->(p2))
        OPTIONAL MATCH (p2CommonAncestorThroughMarriage:Person)
            WHERE ((p2CommonAncestorThroughMarriage)-[*]->(p2) OR p2CommonAncestorThroughMarriage = p2)
              AND ((p2CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p1)
                    OR (p2CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(:Person)-[:PARTNER_IN]->(:Partnership)-[:BEGAT]->(p1))
        OPTIONAL MATCH (onlyMarriageCommonAncestor: Person)
            WHERE (onlyMarriageCommonAncestor)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p1)
              AND (onlyMarriageCommonAncestor)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p2)
        WITH p1, p2
           , directCommonAncestor IS NULL AND (p1CommonAncestorThroughMarriage IS NOT NULL OR (p2CommonAncestorThroughMarriage IS NULL AND onlyMarriageCommonAncestor IS NOT NULL)) AS person2MarriedIn
           , directCommonAncestor IS NULL AND (p2CommonAncestorThroughMarriage IS NOT NULL OR (p1CommonAncestorThroughMarriage IS NULL AND onlyMarriageCommonAncestor IS NOT NULL)) AS person1MarriedIn
           , CASE
                WHEN directCommonAncestor IS NOT NULL THEN directCommonAncestor
                WHEN p1CommonAncestorThroughMarriage IS NOT NULL THEN p1CommonAncestorThroughMarriage
                WHEN p2CommonAncestorThroughMarriage IS NOT NULL THEN p2CommonAncestorThroughMarriage
                ELSE onlyMarriageCommonAncestor
             END AS commonAncestor
        WITH p1, p2
           , commonAncestor
           , person1MarriedIn
           , person2MarriedIn
           , CASE
                WHEN commonAncestor = p1 THEN [p1.id]
                ELSE [n IN nodes(shortestPath((commonAncestor)-[*]-(p1))) | n.id]
             END AS pathIdsFromCommonAncestorToPerson1
           , CASE
                WHEN commonAncestor = p2 THEN [p2.id]
                ELSE [n IN nodes(shortestPath((commonAncestor)-[*]-(p2))) | n.id]
             END AS pathIdsFromCommonAncestorToPerson2
           , CASE
                WHEN commonAncestor = p1 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p1))) WHERE n:Person]) - 1
             END AS person1NumberOfPersonNodesToCommonAncestor
           , CASE
                WHEN commonAncestor = p2 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p2))) WHERE n:Person]) - 1
             END AS person2NumberOfPersonNodesToCommonAncestor
        ORDER BY (NOT (person1MarriedIn OR person2MarriedIn)) DESC, person1NumberOfPersonNodesToCommonAncestor ASC, commonAncestor.sex DESC
        LIMIT 1
        OPTIONAL MATCH (sharedAncestralPartnership:Partnership)
            WHERE (commonAncestor)-[:PARTNER_IN]->(sharedAncestralPartnership)
              AND ((sharedAncestralPartnership)-[*]->(p1) OR (sharedAncestralPartnership)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p1))
              AND ((sharedAncestralPartnership)-[*]->(p2) OR (sharedAncestralPartnership)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p2))
        OPTIONAL MATCH (otherCommonAncestor:Person)
            WHERE (otherCommonAncestor)-[:PARTNER_IN]->(sharedAncestralPartnership)
              AND otherCommonAncestor <> commonAncestor
        RETURN p1.id AS person1Id
             , p2.id AS person2Id
             , p1.sex AS person1Sex
             , p2.sex AS person2Sex
             , person1MarriedIn
             , person2MarriedIn
             , commonAncestor.id AS commonAncestorId
             , otherCommonAncestor.id AS otherCommonAncestorId
             , sharedAncestralPartnership.id AS sharedAncestralPartnershipId
             , person1NumberOfPersonNodesToCommonAncestor
             , person2NumberOfPersonNodesToCommonAncestor
             , pathIdsFromCommonAncestorToPerson1
             , pathIdsFromCommonAncestorToPerson2
    """))
                          .bind(person1Id.toString()).to("person1Id")
                          .bind(person2Id.toString()).to("person2Id")
                          .fetchAs(GenealogicalLink.class)
                          .mappedBy(GenealogicalLink::fromRecord)
                          .one();
    }

    public Optional<UUID> findSpouseViaSpousesAncestor(UUID personId, UUID ancestorId) {
        return neo4jClient.query(("""
        MATCH (a:Person {id: $ancestorId})-[*]->(spouse:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p:Person {id: $personId})
        RETURN spouse.id AS spouseId"""))
                          .bind(personId.toString()).to("personId")
                          .bind(ancestorId.toString()).to("ancestorId")
                          .fetchAs(UUID.class)
                          .one();
    }
}
