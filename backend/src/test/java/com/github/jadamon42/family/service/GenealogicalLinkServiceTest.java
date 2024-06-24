package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Relationship;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
@Import(CustomCypherQueryExecutor.class)
@ActiveProfiles("test")
public class GenealogicalLinkServiceTest {
    @Test
    void getGenealogicalLinkOfNonExistentPerson() {
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(UUID.randomUUID(), personId);
        assertThat(relationship).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfUnrelatedPerson() {
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(personId, unrelatedPersonId);
        assertThat(relationship).isEmpty();
    }
    
    @Test
    void getGenealogicalLinkOfSelf() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, personId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Self");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Self");
    }

    @Test
    void getGenealogicalLinkOfSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, spouseId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Wife");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Husband");
    }

    @Test
    void getGenealogicalLinkOfSon() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, sonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Son");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Father");
    }

    @Test
    void getGenealogicalLinkOfDaughterInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, daughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Daughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Father-in-Law");
    }

    @Test
    void getGenealogicalLinkOfDaughter() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, daughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Daughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Father");
    }

    @Test
    void getGenealogicalLinkOfSonInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, sonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Son-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Father-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepSon() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepSonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Son");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Father");
    }

    @Test
    void getGenealogicalLinkOfStepDaughter() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepDaughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Daughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Father");
    }

    @Test
    void getGenealogicalLinkOfGrandson() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandsonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandson");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandfather");
    }

    @Test
    void getGenealogicalLinkOfGranddaughterInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, granddaughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Granddaughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGranddaughter() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, granddaughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Granddaughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandfather");
    }

    @Test
    void getGenealogicalLinkOfGrandsonInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandsonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandson-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandson() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandsonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandson");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandfather");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughterInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGranddaughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Granddaughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughter() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGranddaughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Granddaughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandfather");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandsonInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandsonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandson-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandfather-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, fatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Father");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Son");
    }

    @Test
    void getGenealogicalLinkOfMother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, motherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Mother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Son");
    }

    @Test
    void getGenealogicalLinkOfStepFather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepFatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Father");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Son");
    }

    @Test
    void getGenealogicalLinkOfStepMother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepMotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Mother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Son");
    }

    @Test
    void getGenealogicalLinkOfBrother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, brotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Brother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Brother");
    }

    @Test
    void getGenealogicalLinkOfSister() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, sisterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Sister");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Brother");
    }

    @Test
    void getGenealogicalLinkOfHalfBrother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, halfBrotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Half-Brother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Half-Brother");
    }

    @Test
    void getGenealogicalLinkOfHalfSister() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, halfSisterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Half-Sister");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Half-Brother");
    }

    @Test
    void getGenealogicalLinkOfStepBrother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepBrotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Brother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Brother");
    }

    @Test
    void getGenealogicalLinkOfStepSister() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepSisterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Sister");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Brother");
    }

    @Test
    void getGenealogicalLinkOfBrotherInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, brotherInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Brother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Brother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSisterInLaw() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, sisterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Sister-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Brother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfNiece() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, nieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Uncle");
    }

    @Test
    void getGenealogicalLinkOfNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, nephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Uncle");
    }

    @Test
    void getGenealogicalLinkOfGreatNiece() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatNieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Uncle");
    }

    @Test
    void getGenealogicalLinkOfGreatNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Uncle");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNiece() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandNieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Uncle");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Uncle");
    }

    @Test
    void getGenealogicalLinkOfGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandson");
    }

    @Test
    void getGenealogicalLinkOfGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Grandson");
    }

    @Test
    void getGenealogicalLinkOfUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, uncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Uncle");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Nephew");
    }

    @Test
    void getGenealogicalLinkOfAunt() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, auntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Aunt");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Nephew");
    }

    @Test
    void getGenealogicalLinkOfFirstCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfGreatUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Uncle");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfGreatAunt() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatAuntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Aunt");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinOnceRemovedThroughGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Great-Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Great-Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGreatGrandfather() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGreatGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Great-Grandfather");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGreatGrandmother() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepGreatGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Great-Grandmother");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Grandson");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Uncle");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAunt() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandAuntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Aunt");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Nephew");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Thrice Removed");
    }

    // Spouse Tests (mostly for in-law relationships)
    @Test
    void getGenealogicalLinkOfSonFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, sonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Son");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Mother");
    }

    @Test
    void getGenealogicalLinkOfDaughterInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, daughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Daughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Mother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfDaughterFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, daughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Daughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Mother");
    }

    @Test
    void getGenealogicalLinkOfSonInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, sonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Son-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Mother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepSonFromSpouse() {
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(spouseId, stepSonId);
        assertThat(relationship).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfStepDaughterFromSpouse() {
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(spouseId, stepDaughterId);
        assertThat(relationship).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfGrandsonFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, grandsonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandson");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandmother");
    }

    @Test
    void getGenealogicalLinkOfGranddaughterInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, granddaughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Granddaughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGranddaughterFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, granddaughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Granddaughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandmother");
    }

    @Test
    void getGenealogicalLinkOfGrandsonInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, grandsonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandson-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandsonFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandsonId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandson");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandmother");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughterInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGranddaughterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Granddaughter-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGranddaughterFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGranddaughterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Granddaughter");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandmother");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandsonInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandsonInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandson-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandmother-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, fatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Father-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Daughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfMotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, motherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Mother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Daughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepFatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepFatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Father-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Daughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepMotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepMotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Mother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Daughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfBrotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, brotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Brother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSisterFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, sisterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Sister-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfHalfBrotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, halfBrotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Half-Brother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Half-Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfHalfSisterFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, halfSisterId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Half-Sister-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Half-Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepBrotherFromSpouse() {
        // This isn't ideal, but it's a tricky problem to solve and not a common use case. I've decided this is expected behavior.
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(spouseId, stepBrotherId);
        assertThat(relationship).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfStepSisterFromSpouse() {
        // This isn't ideal, but it's a tricky problem to solve and not a common use case. I've decided this is expected behavior.
        Optional<Relationship> relationship = genealogicalLinkService.getRelationship(spouseId, stepSisterId);
        assertThat(relationship).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfBrotherInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, brotherInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Brother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSisterInLawFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, sisterInLawId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Sister-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Sister-in-Law");
    }

    @Test
    void getGenealogicalLinkOfNieceFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, nieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Aunt");
    }

    @Test
    void getGenealogicalLinkOfNephewFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, nephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Aunt");
    }

    @Test
    void getGenealogicalLinkOfGreatNieceFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatNieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Aunt");
    }

    @Test
    void getGenealogicalLinkOfGreatNephewFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Aunt");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNieceFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandNieceId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Niece");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Aunt");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNephewFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Nephew");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Aunt");
    }

    @Test
    void getGenealogicalLinkOfGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, grandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, grandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Granddaughter-in-Law");
    }

    @Test @Disabled
    void getGenealogicalLinkOfStepGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfUncleFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, uncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Uncle-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfAuntFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, auntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Aunt-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatUncleFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Uncle-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatAuntFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatAuntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Aunt-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparentsFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinOnceRemovedThroughGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, secondCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, secondCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, secondCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, secondCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Great-Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Great-Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGreatGrandfatherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGreatGreatGrandfatherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Great-Grandfather-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfStepGreatGreatGrandmotherFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, stepGreatGreatGrandmotherId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Great-Great-Grandmother-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Granddaughter-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandUncleFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Uncle-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAuntFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, greatGrandAuntId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Aunt-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Niece-in-Law");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparentsFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin-in-Law Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparentsFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, thirdCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin-in-Law");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin-in-Law");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, thirdCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, thirdCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemovedFromSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(spouseId, thirdCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin-in-Law Thrice Removed");
    }

    @Test
    void needsAttention() {
        Relationship r1 = genealogicalLinkService.getRelationship(greatGrandfatherId, stepDaughterId).orElseThrow();
        assertThat(r1.getRelationshipLabel()).isEqualTo("Step-Great-Great-Granddaughter");
        assertThat(r1.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Grandfather");

//        Relationship r2 = genealogicalLinkService.getRelationship(stepGreatGrandfatherId, stepDaughterId).orElseThrow();
//        assertThat(r2.getRelationshipLabel()).isEqualTo("Step-Great-Great-Granddaughter");
//        assertThat(r2.getInverseRelationshipLabel()).isEqualTo("Step-Great-Great-Grandfather");
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
    GenealogicalLinkServiceTest(CustomCypherQueryExecutor customCypherQueryExecutor) {
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

    static String createPlaceholders(UUID personId) {
        return String.format("""
        CREATE (p1:Person {id: '%s'})-[:PARTNER_IN]->(pt1:Partnership {id: '%s', type: 'PLACEHOLDER'})<-[:PARTNER_IN]-(p2:Person {id: '%s'})
        WITH pt1
        MATCH (p3:Person {id: '%s'})
        CREATE (pt1)-[:BEGAT]->(p3)
        """, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), personId);
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
            session.run(createPlaceholders(motherId));
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
            session.run(createPlaceholders(grandmotherId));
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
            session.run(createPlaceholders(greatGrandmotherId));
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
            session.run(createPlaceholders(greatGreatGrandfatherId));
            session.run(createWoman(greatGreatGrandmotherId, "greatGreatGrandmother"));
            session.run(createPlaceholders(greatGreatGrandmotherId));
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
