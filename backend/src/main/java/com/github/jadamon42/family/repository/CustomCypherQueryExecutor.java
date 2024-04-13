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
        MATCH (p1:Person {id: $person1Id})-[*]-(p2:Person {id: $person2Id})
        OPTIONAL MATCH (directCommonAncestor:Person)
            WHERE ((directCommonAncestor)-[*]->(p1) OR directCommonAncestor = p1)
              AND ((directCommonAncestor)-[*]->(p2) OR directCommonAncestor = p2)
        OPTIONAL MATCH (p1CommonAncestorThroughMarriage:Person)
            WHERE ((p1CommonAncestorThroughMarriage)-[*]->(p1) OR p1CommonAncestorThroughMarriage = p1)
              AND (p1CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p2)
        OPTIONAL MATCH (p2CommonAncestorThroughMarriage:Person)
            WHERE (p2CommonAncestorThroughMarriage)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p1)
              AND ((p2CommonAncestorThroughMarriage)-[*]->(p2) OR p2CommonAncestorThroughMarriage = p2)
        OPTIONAL MATCH (onlyMarriageCommonAncestor: Person)
            WHERE (onlyMarriageCommonAncestor)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p1)
              AND (onlyMarriageCommonAncestor)-[*]->(:Person)-[:PARTNER_IN]->(:Partnership)<-[:PARTNER_IN]-(p2)
        WITH p1, p2
           , directCommonAncestor IS NULL AND (p1CommonAncestorThroughMarriage IS NOT NULL OR onlyMarriageCommonAncestor IS NOT NULL) AS p2MarriedIn
           , directCommonAncestor IS NULL AND (p2CommonAncestorThroughMarriage IS NOT NULL OR onlyMarriageCommonAncestor IS NOT NULL) AS p1MarriedIn
           , CASE
                WHEN directCommonAncestor IS NOT NULL THEN directCommonAncestor
                WHEN p1CommonAncestorThroughMarriage IS NOT NULL THEN p1CommonAncestorThroughMarriage
                WHEN p2CommonAncestorThroughMarriage IS NOT NULL THEN p2CommonAncestorThroughMarriage
                ELSE onlyMarriageCommonAncestor
             END AS commonAncestor
        WITH p1, p2
           , commonAncestor
           , p1MarriedIn
           , p2MarriedIn
           , CASE
                WHEN commonAncestor = p1 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p1))) WHERE n:Person]) - 1
             END AS p1NumberOfPersonNodesToCommonAncestor
           , CASE
                WHEN commonAncestor = p2 THEN 0
                ELSE SIZE([n IN nodes(shortestPath((commonAncestor)-[*]-(p2))) WHERE n:Person]) - 1
             END AS p2NumberOfPersonNodesToCommonAncestor
        ORDER BY (NOT (p1MarriedIn OR p2MarriedIn)) DESC, p1NumberOfPersonNodesToCommonAncestor ASC, commonAncestor.sex DESC
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
             , commonAncestor.id AS commonAncestorId
             , otherCommonAncestor.id AS otherCommonAncestorId
             , sharedAncestralPartnership.id AS sharedAncestralPartnershipId
             , p1NumberOfPersonNodesToCommonAncestor
             , p2NumberOfPersonNodesToCommonAncestor
             , p1MarriedIn
             , p2MarriedIn
        """))
                          .bind(person1Id).to("person1Id")
                          .bind(person2Id).to("person2Id")
                          .fetchAs(GenealogicalLink.class)
                          .mappedBy(GenealogicalLink::fromRecord)
                          .one();
    }
}
