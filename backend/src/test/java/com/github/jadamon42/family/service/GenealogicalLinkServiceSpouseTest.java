package com.github.jadamon42.family.service;

import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
@Import(CustomCypherQueryExecutor.class)
public class GenealogicalLinkServiceSpouseTest { 
    @Test
    void getGenealogicalLinkOfSon() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, sonId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Son");
    }

    @Test
    void getGenealogicalLinkOfDaughterInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, daughterInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Daughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfDaughter() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, daughterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Daughter");
    }

    @Test
    void getGenealogicalLinkOfSonInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, sonInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Son-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepSon() {
        Optional<String> relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepSonId);
        assertThat(relationshipLabel).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfStepDaughter() {
        Optional<String> relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepDaughterId);
        assertThat(relationshipLabel).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfGrandson() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, grandsonId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandson");
    }

    @Test
    void getGenealogicalLinkOfGranddaughterInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, granddaughterInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGranddaughter() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, granddaughterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Granddaughter");
    }

    @Test
    void getGenealogicalLinkOfGrandsonInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, grandsonInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandson-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandson() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandsonId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughterInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGranddaughterInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughter() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGranddaughterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Granddaughter");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandsonInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandsonInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandson-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, fatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Father-in-Law");
    }

    @Test
    void getGenealogicalLinkOfMother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, motherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Mother-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepFather() {
        // 'mother' doesn't have a parent, so we can't find a common ancestor between 'mother' and 'person'
        // we find our relationship with 'step-father' through 'mother'
        // possible solution: make a null person node be the root for all non-begat persons? Don't like it.
        // Just say not related?
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepFatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Father-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepMother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepMotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Mother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfBrother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, brotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Brother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSister() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, sisterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Sister-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfHalfBrother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, halfBrotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Half-Brother-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfHalfSister() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, halfSisterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Half-Sister-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepBrother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepBrotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Brother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepSister() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepSisterId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfBrotherInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, brotherInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Brother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSisterInLaw() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, sisterInLawId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfNiece() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, nieceId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Niece");
    }

    @Test
    void getGenealogicalLinkOfNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, nephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Nephew");
    }

    @Test
    void getGenealogicalLinkOfGreatNiece() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatNieceId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Niece");
    }

    @Test
    void getGenealogicalLinkOfGreatNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatNephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNiece() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandNieceId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Niece");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandNephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, grandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGrandmother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, grandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandmother-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGrandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGrandmother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGrandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, uncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Uncle-in-Law");
    }

    @Test
    void getGenealogicalLinkOfAunt() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, auntId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Aunt-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandmother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandmother-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepGreatGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGreatGrandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Great-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGrandmother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGreatGrandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Great-Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatUncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Uncle-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatAunt() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatAuntId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Aunt-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinOnceRemovedThroughGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, secondCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, secondCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, secondCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, secondCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin-in-Law Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGreatGrandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Great-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandmother() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGreatGrandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Great-Grandmother-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepGreatGreatGrandfather() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGreatGreatGrandfatherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Great-Great-Grandfather-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepGreatGreatGrandmother() {
        // need null object for this too
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, stepGreatGreatGrandmotherId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Great-Great-Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandUncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Uncle-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAunt() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, greatGrandAuntId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Aunt-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, thirdCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, thirdCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, thirdCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(spouseId, thirdCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin-in-Law Thrice Removed");
    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final GenealogicalLinkService genealogicalLinkService;
    
    private static final UUID personId = UUID.randomUUID();
    private static final UUID spouseId = UUID.randomUUID();
    private static final UUID sonId = UUID.randomUUID();
    private static final UUID daughterInLawId = UUID.randomUUID();
    private static final UUID daughterId = UUID.randomUUID();
    private static final UUID sonInLawId = UUID.randomUUID();
    private static final UUID otherSpouseId = UUID.randomUUID();
    private static final UUID stepSonId = UUID.randomUUID();
    private static final UUID stepDaughterId = UUID.randomUUID();
    private static final UUID grandsonId = UUID.randomUUID();
    private static final UUID granddaughterInLawId = UUID.randomUUID();
    private static final UUID granddaughterId = UUID.randomUUID();
    private static final UUID grandsonInLawId = UUID.randomUUID();
    private static final UUID greatGrandsonId = UUID.randomUUID();
    private static final UUID greatGranddaughterInLawId = UUID.randomUUID();
    private static final UUID greatGranddaughterId = UUID.randomUUID();
    private static final UUID greatGrandsonInLawId = UUID.randomUUID();
    private static final UUID fatherId = UUID.randomUUID();
    private static final UUID motherId = UUID.randomUUID();
    private static final UUID stepFatherId = UUID.randomUUID();
    private static final UUID stepMotherId = UUID.randomUUID();
    private static final UUID brotherId = UUID.randomUUID();
    private static final UUID sisterId = UUID.randomUUID();
    private static final UUID halfBrotherId = UUID.randomUUID();
    private static final UUID halfSisterId = UUID.randomUUID();
    private static final UUID stepBrotherId = UUID.randomUUID();
    private static final UUID stepSisterId = UUID.randomUUID();
    private static final UUID brotherInLawId = UUID.randomUUID();
    private static final UUID sisterInLawId = UUID.randomUUID();
    private static final UUID nieceId = UUID.randomUUID();
    private static final UUID nephewId = UUID.randomUUID();
    private static final UUID greatNieceId = UUID.randomUUID();
    private static final UUID greatNephewId = UUID.randomUUID();
    private static final UUID greatGrandNieceId = UUID.randomUUID();
    private static final UUID greatGrandNephewId = UUID.randomUUID();
    private static final UUID grandfatherId = UUID.randomUUID();
    private static final UUID grandmotherId = UUID.randomUUID();
    private static final UUID stepGrandfatherId = UUID.randomUUID();
    private static final UUID stepGrandmotherId = UUID.randomUUID();
    private static final UUID uncleId = UUID.randomUUID();
    private static final UUID auntId = UUID.randomUUID();
    private static final UUID firstCousinId = UUID.randomUUID();
    private static final UUID firstCousinOnceRemovedId = UUID.randomUUID();
    private static final UUID firstCousinTwiceRemovedId = UUID.randomUUID();
    private static final UUID firstCousinThriceRemovedId = UUID.randomUUID();
    private static final UUID greatGrandfatherId = UUID.randomUUID();
    private static final UUID greatGrandmotherId = UUID.randomUUID();
    private static final UUID stepGreatGrandfatherId = UUID.randomUUID();
    private static final UUID stepGreatGrandmotherId = UUID.randomUUID();
    private static final UUID greatUncleId = UUID.randomUUID();
    private static final UUID greatAuntId = UUID.randomUUID();
    private static final UUID firstCousinOnceRemovedThroughGreatGrandparentsId = UUID.randomUUID();
    private static final UUID secondCousinId = UUID.randomUUID();
    private static final UUID secondCousinOnceRemovedId = UUID.randomUUID();
    private static final UUID secondCousinTwiceRemovedId = UUID.randomUUID();
    private static final UUID secondCousinThriceRemovedId = UUID.randomUUID();
    private static final UUID greatGreatGrandfatherId = UUID.randomUUID();
    private static final UUID greatGreatGrandmotherId = UUID.randomUUID();
    private static final UUID stepGreatGreatGrandfatherId = UUID.randomUUID();
    private static final UUID stepGreatGreatGrandmotherId = UUID.randomUUID();
    private static final UUID greatGrandUncleId = UUID.randomUUID();
    private static final UUID greatGrandAuntId = UUID.randomUUID();
    private static final UUID firstCousinTwiceRemovedThroughGreatGreatGrandparentsId = UUID.randomUUID();
    private static final UUID secondCousinOnceRemovedThroughGreatGreatGrandparentsId = UUID.randomUUID();
    private static final UUID thirdCousinId = UUID.randomUUID();
    private static final UUID thirdCousinOnceRemovedId = UUID.randomUUID();
    private static final UUID thirdCousinTwiceRemovedId = UUID.randomUUID();
    private static final UUID thirdCousinThriceRemovedId = UUID.randomUUID();
    private static final UUID unrelatedPersonId = UUID.randomUUID();
    
    @Autowired
    GenealogicalLinkServiceSpouseTest(CustomCypherQueryExecutor customCypherQueryExecutor) {
        this.genealogicalLinkService = new GenealogicalLinkService(customCypherQueryExecutor);
    }

    static String createMan(UUID id, String label) {
        return String.format("CREATE (%s:Person {id: '%s', firstName: '%s', lastName: '%s', sex: 'Male'})", label, id, label, label);
    }

    static String createWoman(UUID id, String label) {
        return String.format("CREATE (%s:Person {id: '%s', firstName: '%s', lastName: '%s', sex: 'Female'})", label, id, label, label);
    }

    static String createPartnership(UUID personId, List<UUID> childrenIds) {
        StringBuilder query = new StringBuilder();
        query.append(String.format("""
        MATCH (p1:Person {id: '%s'})
        CREATE (p1)-[:PARTNER_IN]->(p:Partnership {id: '%s'})
        """, personId, UUID.randomUUID()));

        if (!childrenIds.isEmpty()) {
            query.append("\nWITH p\n");
        }

        for (int i = 0; i < childrenIds.size(); i++) {
            query.append(
                    String.format("""
                    MATCH (p%s:Person {id: '%s'})
                    CREATE (p)-[:BEGAT]->(p%s)
                    """, i+2, childrenIds.get(i), i+2));
            if (i != childrenIds.size() - 1) {
                query.append("\nWITH p\n");
            }
        }

        return query.toString();
    }
    
    static String createPartnership(UUID person1Id, UUID person2Id) {
        return String.format("""
        MATCH (p1:Person {id: '%s'})
        OPTIONAL MATCH (p2:Person {id: '%s'})
        CREATE (p1)-[:PARTNER_IN]->(p:Partnership {id: '%s'})<-[:PARTNER_IN]-(p2)
        """, person1Id, person2Id, UUID.randomUUID());
    }

    static String createPartnership(UUID person1Id, UUID person2Id, List<UUID> childrenIds) {
        StringBuilder query = new StringBuilder();
        query.append(String.format("""
        MATCH (p1:Person {id: '%s'})
        OPTIONAL MATCH (p2:Person {id: '%s'})
        CREATE (p1)-[:PARTNER_IN]->(p:Partnership {id: '%s'})<-[:PARTNER_IN]-(p2)
        """, person1Id, person2Id, UUID.randomUUID()));

        if (!childrenIds.isEmpty()) {
            query.append("\nWITH p\n");
        }


        for (int i = 0; i < childrenIds.size(); i++) {
            query.append(
                    String.format("""
                    MATCH (p%s:Person {id: '%s'})
                    CREATE (p)-[:BEGAT]->(p%s)
                    """, i+2, childrenIds.get(i), i+2));
            if (i != childrenIds.size() - 1) {
                query.append("\nWITH p\n");
            }
        }

        return query.toString();
    }

    @BeforeAll
    static void setUp() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
        neo4jDriver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), AuthTokens.none());

        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
            // 0
            session.run(createMan(personId, "person"));
            session.run(createWoman(spouseId, "spouse"));
            session.run(createMan(sonId, "son"));
            session.run(createWoman(daughterInLawId, "daughterInLaw"));
            session.run(createWoman(daughterId, "daughter"));
            session.run(createMan(sonInLawId, "sonInLaw"));
            session.run(createWoman(otherSpouseId, "otherSpouse"));
            session.run(createMan(stepSonId, "stepSon"));
            session.run(createWoman(stepDaughterId, "stepDaughter"));
            session.run(createMan(grandsonId, "grandson"));
            session.run(createWoman(granddaughterInLawId, "granddaughterInLaw"));
            session.run(createWoman(granddaughterId, "granddaughter"));
            session.run(createMan(grandsonInLawId, "grandsonInLaw"));
            session.run(createMan(greatGrandsonId, "greatGrandson"));
            session.run(createWoman(greatGranddaughterInLawId, "greatGranddaughterInLaw"));
            session.run(createWoman(greatGranddaughterId, "greatGranddaughter"));
            session.run(createMan(greatGrandsonInLawId, "greatGrandson"));
            // 1
            session.run(createMan(fatherId, "father"));
            session.run(createWoman(motherId, "mother"));
            session.run(createMan(stepFatherId, "stepFather"));
            session.run(createWoman(stepMotherId, "stepMother"));
            session.run(createMan(brotherId, "brother"));
            session.run(createWoman(sisterId, "sister"));
            session.run(createMan(halfBrotherId, "halfBrother"));
            session.run(createWoman(halfSisterId, "halfSister"));
            session.run(createMan(stepBrotherId, "stepBrother"));
            session.run(createWoman(stepSisterId, "stepSister"));
            session.run(createMan(brotherInLawId, "brotherInLaw"));
            session.run(createWoman(sisterInLawId, "sisterInLaw"));
            session.run(createWoman(nieceId, "niece"));
            session.run(createMan(nephewId, "nephew"));
            session.run(createWoman(greatNieceId, "greatNiece"));
            session.run(createMan(greatNephewId, "greatNephew"));
            session.run(createWoman(greatGrandNieceId, "greatGrandNiece"));
            session.run(createMan(greatGrandNephewId, "greatGrandNephew"));
            // 2
            session.run(createMan(grandfatherId, "grandfather"));
            session.run(createWoman(grandmotherId, "grandmother"));
            session.run(createMan(stepGrandfatherId, "stepGrandfather"));
            session.run(createWoman(stepGrandmotherId, "stepGrandmother"));
            session.run(createMan(uncleId, "uncle"));
            session.run(createWoman(auntId, "aunt"));
            session.run(createMan(firstCousinId, "firstCousin"));
            session.run(createWoman(firstCousinOnceRemovedId, "firstCousinOnceRemoved"));
            session.run(createMan(firstCousinTwiceRemovedId, "firstCousinTwiceRemoved"));
            session.run(createWoman(firstCousinThriceRemovedId, "firstCousinThriceRemoved"));
            // 3
            session.run(createMan(greatGrandfatherId, "greatGrandfather"));
            session.run(createWoman(greatGrandmotherId, "greatGrandmother"));
            session.run(createMan(stepGreatGrandfatherId, "stepGreatGrandfather"));
            session.run(createWoman(stepGreatGrandmotherId, "stepGreatGrandmother"));
            session.run(createMan(greatUncleId, "greatUncle"));
            session.run(createWoman(greatAuntId, "greatAunt"));
            session.run(createMan(firstCousinOnceRemovedThroughGreatGrandparentsId, "firstCousinOnceRemovedThroughGreatGrandparents"));
            session.run(createMan(secondCousinId, "secondCousin"));
            session.run(createWoman(secondCousinOnceRemovedId, "secondCousinOnceRemoved"));
            session.run(createMan(secondCousinTwiceRemovedId, "secondCousinTwiceRemoved"));
            session.run(createWoman(secondCousinThriceRemovedId, "secondCousinThriceRemoved"));
            // 4
            session.run(createMan(greatGreatGrandfatherId, "greatGreatGrandfather"));
            session.run(createWoman(greatGreatGrandmotherId, "greatGreatGrandmother"));
            session.run(createMan(stepGreatGreatGrandfatherId, "stepGreatGreatGrandfather"));
            session.run(createWoman(stepGreatGreatGrandmotherId, "stepGreatGreatGrandmother"));
            session.run(createMan(greatGrandUncleId, "greatGrandUncle"));
            session.run(createWoman(greatGrandAuntId, "greatGrandAunt"));
            session.run(createMan(firstCousinTwiceRemovedThroughGreatGreatGrandparentsId, "firstCousinTwiceRemovedThroughGreatGreatGrandparents"));
            session.run(createMan(secondCousinOnceRemovedThroughGreatGreatGrandparentsId, "secondCousinOnceRemovedThroughGreatGreatGrandparents"));
            session.run(createMan(thirdCousinId, "thirdCousin"));
            session.run(createWoman(thirdCousinOnceRemovedId, "thirdCousinOnceRemoved"));
            session.run(createMan(thirdCousinTwiceRemovedId, "thirdCousinTwiceRemoved"));
            session.run(createWoman(thirdCousinThriceRemovedId, "thirdCousinThriceRemoved"));
            session.run(createMan(unrelatedPersonId, "unrelatedPerson"));
            // Common Ancestor 0 - Partnerships
            session.run(createPartnership(personId, spouseId, List.of(sonId, daughterId)));
            session.run(createPartnership(sonId, daughterInLawId, List.of(grandsonId)));
            session.run(createPartnership(daughterId, sonInLawId, List.of(granddaughterId)));
            session.run(createPartnership(otherSpouseId, List.of(stepSonId, stepDaughterId)));
            session.run(createPartnership(personId, otherSpouseId));
            session.run(createPartnership(grandsonId, granddaughterInLawId, List.of(greatGrandsonId)));
            session.run(createPartnership(granddaughterId, grandsonInLawId, List.of(greatGranddaughterId)));
            session.run(createPartnership(greatGrandsonId, greatGranddaughterInLawId));
            session.run(createPartnership(greatGranddaughterId, greatGrandsonInLawId));
            // Common Ancestor 1 - Partnerships
            session.run(createPartnership(fatherId, motherId, List.of(personId, brotherId, sisterId)));
            session.run(createPartnership(stepFatherId, List.of(stepBrotherId)));
            session.run(createPartnership(stepMotherId, List.of(stepSisterId)));
            session.run(createPartnership(fatherId, stepMotherId, List.of(halfBrotherId)));
            session.run(createPartnership(motherId, stepFatherId, List.of(halfSisterId)));
            session.run(createPartnership(brotherId, sisterInLawId, List.of(nephewId)));
            session.run(createPartnership(sisterId, brotherInLawId, List.of(nieceId)));
            session.run(createPartnership(nephewId, List.of(greatNephewId)));
            session.run(createPartnership(nieceId, List.of(greatNieceId)));
            session.run(createPartnership(greatNephewId, List.of(greatGrandNephewId)));
            session.run(createPartnership(greatNieceId, List.of(greatGrandNieceId)));
            // Common Ancestor 2 - Partnerships
            session.run(createPartnership(grandfatherId, grandmotherId, List.of(fatherId, uncleId, auntId)));
            session.run(createPartnership(grandfatherId, stepGrandmotherId));
            session.run(createPartnership(grandmotherId, stepGrandfatherId));
            session.run(createPartnership(uncleId, List.of(firstCousinId)));
            session.run(createPartnership(firstCousinId, List.of(firstCousinOnceRemovedId)));
            session.run(createPartnership(firstCousinOnceRemovedId, List.of(firstCousinTwiceRemovedId)));
            session.run(createPartnership(firstCousinTwiceRemovedId, List.of(firstCousinThriceRemovedId)));
            // Common Ancestor 3 - Partnerships
            session.run(createPartnership(greatGrandfatherId, greatGrandmotherId, List.of(grandfatherId, greatUncleId, greatAuntId)));
            session.run(createPartnership(greatGrandfatherId, stepGreatGrandmotherId));
            session.run(createPartnership(greatGrandmotherId, stepGreatGrandfatherId));
            session.run(createPartnership(greatUncleId, List.of(firstCousinOnceRemovedThroughGreatGrandparentsId)));
            session.run(createPartnership(firstCousinOnceRemovedThroughGreatGrandparentsId, List.of(secondCousinId)));
            session.run(createPartnership(secondCousinId, List.of(secondCousinOnceRemovedId)));
            session.run(createPartnership(secondCousinOnceRemovedId, List.of(secondCousinTwiceRemovedId)));
            session.run(createPartnership(secondCousinTwiceRemovedId, List.of(secondCousinThriceRemovedId)));
            // Common Ancestor 4 - Partnerships
            session.run(createPartnership(greatGreatGrandfatherId, greatGreatGrandmotherId, List.of(greatGrandfatherId, greatGrandUncleId)));
            session.run(createPartnership(greatGreatGrandfatherId, stepGreatGreatGrandmotherId));
            session.run(createPartnership(greatGreatGrandmotherId, stepGreatGreatGrandfatherId));
            session.run(createPartnership(greatGrandUncleId, greatGrandAuntId, List.of(firstCousinTwiceRemovedThroughGreatGreatGrandparentsId)));
            session.run(createPartnership(firstCousinTwiceRemovedThroughGreatGreatGrandparentsId, List.of(secondCousinOnceRemovedThroughGreatGreatGrandparentsId)));
            session.run(createPartnership(secondCousinOnceRemovedThroughGreatGreatGrandparentsId, List.of(thirdCousinId)));
            session.run(createPartnership(thirdCousinId, List.of(thirdCousinOnceRemovedId)));
            session.run(createPartnership(thirdCousinOnceRemovedId, List.of(thirdCousinTwiceRemovedId)));
            session.run(createPartnership(thirdCousinTwiceRemovedId, List.of(thirdCousinThriceRemovedId)));
        }
    }

    @AfterAll
    static void stopNeo4j() {
        embeddedDatabaseServer.close();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> null);
    }
}
