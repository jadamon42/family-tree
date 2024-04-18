package com.github.jadamon42.family.service;

import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
@Import(CustomCypherQueryExecutor.class)
public class GenealogicalLinkServiceSexlessTest {
    @Test
    void getGenealogicalLinkOfNonExistentPerson() {
        Optional<String> relationshipLabel = genealogicalLinkService.getRelationshipLabel(UUID.randomUUID(), personId);
        assertThat(relationshipLabel).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfUnrelatedPerson() {
        Optional<String> relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, unrelatedPersonId);
        assertThat(relationshipLabel).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfSelf() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, personId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Self");
    }

    @Test
    void getGenealogicalLinkOfSpouse() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, spouseId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Spouse");
    }

    @Test
    void getGenealogicalLinkOfChild() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, childId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Child");
    }

    @Test
    void getGenealogicalLinkOfGrandchild() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, grandchildId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandchild");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandchild() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatGrandchildId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandchild");
    }

    @Test
    void getGenealogicalLinkOfParent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, parentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Parent");
    }

    @Test
    void getGenealogicalLinkOfSibling() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, siblingId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Sibling");
    }

    @Test
    void getGenealogicalLinkOfNieceOrNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, nieceOrNephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Nibling");
    }

    @Test
    void getGenealogicalLinkOfGreatNieceOrNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatNieceOrNephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Nibling");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNieceOrNephew() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatGrandNieceOrNephewId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Nibling");
    }

    @Test
    void getGenealogicalLinkOfGrandparent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, grandparentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grandparent");
    }

    @Test
    void getGenealogicalLinkOfAuntOrUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, auntOrUncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Pibling");
    }

    @Test
    void getGenealogicalLinkOfFirstCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandparent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatGrandparentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grandparent");
    }

    @Test
    void getGenealogicalLinkOfGreatAuntOrUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatAuntOrUncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Grand-Pibling");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinOnceRemovedThroughGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, secondCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, secondCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, secondCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, secondCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandparent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatGreatGrandparentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Great-Grandparent");
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAuntOrUncle() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, greatGrandAuntOrUncleId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Great-Grand-Pibling");
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("1st Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparents() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("2nd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousin() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, thirdCousinId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, thirdCousinOnceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin Once Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, thirdCousinTwiceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin Twice Removed");
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemoved() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, thirdCousinThriceRemovedId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("3rd Cousin Thrice Removed");
    }

    @Test
    void getGenealogicalLinkOfOtherParent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, otherParentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Parent");
    }

    @Test
    void getGenealogicalLinkOfStepParent() {
        String relationshipLabel = genealogicalLinkService.getRelationshipLabel(personId, stepParentId).orElseThrow();
        assertThat(relationshipLabel).isEqualTo("Step-Parent");
    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final GenealogicalLinkService genealogicalLinkService;

    private final UUID personId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final UUID spouseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID childId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID grandchildId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final UUID greatGrandchildId = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private final UUID parentId = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private final UUID siblingId = UUID.fromString("00000000-0000-0000-0000-000000000006");
    private final UUID nieceOrNephewId = UUID.fromString("00000000-0000-0000-0000-000000000007");
    private final UUID greatNieceOrNephewId = UUID.fromString("00000000-0000-0000-0000-000000000008");
    private final UUID greatGrandNieceOrNephewId = UUID.fromString("00000000-0000-0000-0000-000000000009");
    private final UUID grandparentId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID auntOrUncleId = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private final UUID firstCousinId = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private final UUID firstCousinOnceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000013");
    private final UUID firstCousinTwiceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000014");
    private final UUID firstCousinThriceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000015");
    private final UUID greatGrandparentId = UUID.fromString("00000000-0000-0000-0000-000000000016");
    private final UUID greatAuntOrUncleId = UUID.fromString("00000000-0000-0000-0000-000000000017");
    private final UUID firstCousinOnceRemovedThroughGreatGrandparentsId = UUID.fromString("00000000-0000-0000-0000-000000000018");
    private final UUID secondCousinId = UUID.fromString("00000000-0000-0000-0000-000000000019");
    private final UUID secondCousinOnceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final UUID secondCousinTwiceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000021");
    private final UUID secondCousinThriceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000022");
    private final UUID greatGreatGrandparentId = UUID.fromString("00000000-0000-0000-0000-000000000023");
    private final UUID greatGrandAuntOrUncleId = UUID.fromString("00000000-0000-0000-0000-000000000024");
    private final UUID firstCousinTwiceRemovedThroughGreatGreatGrandparentsId = UUID.fromString("00000000-0000-0000-0000-000000000025");
    private final UUID secondCousinOnceRemovedThroughGreatGreatGrandparentsId = UUID.fromString("00000000-0000-0000-0000-000000000026");
    private final UUID thirdCousinId = UUID.fromString("00000000-0000-0000-0000-000000000027");
    private final UUID thirdCousinOnceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000028");
    private final UUID thirdCousinTwiceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000029");
    private final UUID thirdCousinThriceRemovedId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private final UUID unrelatedPersonId = UUID.fromString("00000000-0000-0000-0000-000000000031");
    private final UUID otherParentId = UUID.fromString("00000000-0000-0000-0000-000000000032");
    private final UUID stepParentId = UUID.fromString("00000000-0000-0000-0000-000000000033");

    private final UUID spousePartnershipId = UUID.fromString("00000001-0000-0000-0000-000000000000");
    private final UUID childPartnershipId = UUID.fromString("00000002-0000-0000-0000-000000000000");
    private final UUID grandchildPartnershipId = UUID.fromString("00000003-0000-0000-0000-000000000000");
    private final UUID greatGrandchildPartnershipId = UUID.fromString("00000004-0000-0000-0000-000000000000");
    private final UUID parentPartnershipId = UUID.fromString("00000005-0000-0000-0000-000000000000");
    private final UUID siblingPartnershipId = UUID.fromString("00000006-0000-0000-0000-000000000000");
    private final UUID nieceOrNephewPartnershipId = UUID.fromString("00000007-0000-0000-0000-000000000000");
    private final UUID greatNieceOrNephewPartnershipId = UUID.fromString("00000008-0000-0000-0000-000000000000");
    private final UUID greatGrandNieceOrNephewPartnershipId = UUID.fromString("00000009-0000-0000-0000-000000000000");
    private final UUID grandparentPartnershipId = UUID.fromString("00000010-0000-0000-0000-000000000000");
    private final UUID auntOrUnclePartnershipId = UUID.fromString("00000011-0000-0000-0000-000000000000");
    private final UUID firstCousinPartnershipId = UUID.fromString("00000012-0000-0000-0000-000000000000");
    private final UUID firstCousinOnceRemovedPartnershipId = UUID.fromString("00000013-0000-0000-0000-000000000000");
    private final UUID firstCousinTwiceRemovedPartnershipId = UUID.fromString("00000014-0000-0000-0000-000000000000");
    private final UUID firstCousinThriceRemovedPartnershipId = UUID.fromString("00000015-0000-0000-0000-000000000000");
    private final UUID greatGrandparentPartnershipId = UUID.fromString("00000016-0000-0000-0000-000000000000");
    private final UUID greatAuntOrUnclePartnershipId = UUID.fromString("00000017-0000-0000-0000-000000000000");
    private final UUID firstCousinOnceRemovedThroughGreatGrandparentsPartnershipId = UUID.fromString("00000018-0000-0000-0000-000000000000");
    private final UUID secondCousinPartnershipId = UUID.fromString("00000019-0000-0000-0000-000000000000");
    private final UUID secondCousinOnceRemovedPartnershipId = UUID.fromString("00000020-0000-0000-0000-000000000000");
    private final UUID secondCousinTwiceRemovedPartnershipId = UUID.fromString("00000021-0000-0000-0000-000000000000");
    private final UUID secondCousinThriceRemovedPartnershipId = UUID.fromString("00000022-0000-0000-0000-000000000000");
    private final UUID greatGreatGrandparentPartnershipId = UUID.fromString("00000023-0000-0000-0000-000000000000");
    private final UUID greatGrandAuntOrUnclePartnershipId = UUID.fromString("00000024-0000-0000-0000-000000000000");
    private final UUID firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnershipId = UUID.fromString("00000025-0000-0000-0000-000000000000");
    private final UUID secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnershipId = UUID.fromString("00000026-0000-0000-0000-000000000000");
    private final UUID thirdCousinPartnershipId = UUID.fromString("00000027-0000-0000-0000-000000000000");
    private final UUID thirdCousinOnceRemovedPartnershipId = UUID.fromString("00000028-0000-0000-0000-000000000000");
    private final UUID thirdCousinTwiceRemovedPartnershipId = UUID.fromString("00000029-0000-0000-0000-000000000000");
    private final UUID thirdCousinThriceRemovedPartnershipId = UUID.fromString("00000030-0000-0000-0000-000000000000");
    private final UUID parentSecondMarriagePartnershipId = UUID.fromString("00000031-0000-0000-0000-000000000000");

    @Autowired
    GenealogicalLinkServiceSexlessTest(CustomCypherQueryExecutor customCypherQueryExecutor) {
        this.genealogicalLinkService = new GenealogicalLinkService(customCypherQueryExecutor);
    }

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
        neo4jDriver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), AuthTokens.none());
    }

    @BeforeEach
    void setUp() {
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
            session.run("""
            CREATE (person: Person {id: '00000000-0000-0000-0000-000000000000', firstName: 'Person', lastName: 'Person'})
            CREATE (spouse: Person {id: '00000000-0000-0000-0000-000000000001', firstName: 'Spouse', lastName: 'Spouse'})
            CREATE (child: Person {id: '00000000-0000-0000-0000-000000000002', firstName: 'Child', lastName: 'Child'})
            CREATE (grandchild: Person {id: '00000000-0000-0000-0000-000000000003', firstName: 'Grandchild', lastName: 'Grandchild'})
            CREATE (greatGrandchild: Person {id: '00000000-0000-0000-0000-000000000004', firstName: 'GreatGrandchild', lastName: 'GreatGrandchild'})
            CREATE (parent: Person {id: '00000000-0000-0000-0000-000000000005', firstName: 'Parent', lastName: 'Parent'})
            CREATE (sibling: Person {id: '00000000-0000-0000-0000-000000000006', firstName: 'Sibling', lastName: 'Sibling'})
            CREATE (nieceOrNephew: Person {id: '00000000-0000-0000-0000-000000000007', firstName: 'NieceOrNephew', lastName: 'NieceOrNephew'})
            CREATE (greatNieceOrNephew: Person {id: '00000000-0000-0000-0000-000000000008', firstName: 'GreatNieceOrNephew', lastName: 'GreatNieceOrNephew'})
            CREATE (greatGrandNieceOrNephew: Person {id: '00000000-0000-0000-0000-000000000009', firstName: 'GreatGrandNieceOrNephew', lastName: 'GreatGrandNieceOrNephew'})
            CREATE (grandparent: Person {id: '00000000-0000-0000-0000-000000000010', firstName: 'Grandparent', lastName: 'Grandparent'})
            CREATE (auntOrUncle: Person {id: '00000000-0000-0000-0000-000000000011', firstName: 'AuntOrUncle', lastName: 'AuntOrUncle'})
            CREATE (firstCousin: Person {id: '00000000-0000-0000-0000-000000000012', firstName: 'FirstCousin', lastName: 'FirstCousin'})
            CREATE (firstCousinOnceRemoved: Person {id: '00000000-0000-0000-0000-000000000013', firstName: 'FirstCousinOnceRemoved', lastName: 'FirstCousinOnceRemoved'})
            CREATE (firstCousinTwiceRemoved: Person {id: '00000000-0000-0000-0000-000000000014', firstName: 'FirstCousinTwiceRemoved', lastName: 'FirstCousinTwiceRemoved'})
            CREATE (firstCousinThriceRemoved: Person {id: '00000000-0000-0000-0000-000000000015', firstName: 'FirstCousinThriceRemoved', lastName: 'FirstCousinThriceRemoved'})
            CREATE (greatGrandparent: Person {id: '00000000-0000-0000-0000-000000000016', firstName: 'GreatGrandparent', lastName: 'GreatGrandparent'})
            CREATE (greatAuntOrUncle: Person {id: '00000000-0000-0000-0000-000000000017', firstName: 'GreatAuntOrUncle', lastName: 'GreatAuntOrUncle'})
            CREATE (firstCousinOnceRemovedThroughGreatGrandparents: Person {id: '00000000-0000-0000-0000-000000000018', firstName: 'FirstCousinOnceRemovedThroughGreatGrandparents', lastName: 'FirstCousinOnceRemovedThroughGreatGrandparents'})
            CREATE (secondCousin: Person {id: '00000000-0000-0000-0000-000000000019', firstName: 'SecondCousin', lastName: 'SecondCousin'})
            CREATE (secondCousinOnceRemoved: Person {id: '00000000-0000-0000-0000-000000000020', firstName: 'SecondCousinOnceRemoved', lastName: 'SecondCousinOnceRemoved'})
            CREATE (secondCousinTwiceRemoved: Person {id: '00000000-0000-0000-0000-000000000021', firstName: 'SecondCousinTwiceRemoved', lastName: 'SecondCousinTwiceRemoved'})
            CREATE (secondCousinThriceRemoved: Person {id: '00000000-0000-0000-0000-000000000022', firstName: 'SecondCousinThriceRemoved', lastName: 'SecondCousinThriceRemoved'})
            CREATE (greatGreatGrandparent: Person {id: '00000000-0000-0000-0000-000000000023', firstName: 'GreatGreatGrandparent', lastName: 'GreatGreatGrandparent'})
            CREATE (greatGrandAuntOrUncle: Person {id: '00000000-0000-0000-0000-000000000024', firstName: 'GreatGrandAuntOrUncle', lastName: 'GreatGrandAuntOrUncle'})
            CREATE (firstCousinTwiceRemovedThroughGreatGreatGrandparents: Person {id: '00000000-0000-0000-0000-000000000025', firstName: 'FirstCousinTwiceRemovedThroughGreatGreatGrandparents', lastName: 'FirstCousinTwiceRemovedThroughGreatGreatGrandparents'})
            CREATE (secondCousinOnceRemovedThroughGreatGreatGrandparents: Person {id: '00000000-0000-0000-0000-000000000026', firstName: 'SecondCousinOnceRemovedThroughGreatGreatGrandparents', lastName: 'SecondCousinOnceRemovedThroughGreatGreatGrandparents'})
            CREATE (thirdCousin: Person {id: '00000000-0000-0000-0000-000000000027', firstName: 'ThirdCousin', lastName: 'ThirdCousin'})
            CREATE (thirdCousinOnceRemoved: Person {id: '00000000-0000-0000-0000-000000000028', firstName: 'ThirdCousinOnceRemoved', lastName: 'ThirdCousinOnceRemoved'})
            CREATE (thirdCousinTwiceRemoved: Person {id: '00000000-0000-0000-0000-000000000029', firstName: 'ThirdCousinTwiceRemoved', lastName: 'ThirdCousinTwiceRemoved'})
            CREATE (thirdCousinThriceRemoved: Person {id: '00000000-0000-0000-0000-000000000030', firstName: 'ThirdCousinThriceRemoved', lastName: 'ThirdCousinThriceRemoved'})
            CREATE (unrelated: Person {id: '00000000-0000-0000-0000-000000000031', firstName: 'Unrelated', lastName: 'Unrelated', sex: 'female'})
            CREATE (otherParent: Person {id: '00000000-0000-0000-0000-000000000032', firstName: 'Spouse1OfParent', lastName: 'Spouse1OfParent'})
            CREATE (stepParent: Person {id: '00000000-0000-0000-0000-000000000033', firstName: 'Spouse2OfParent', lastName: 'Spouse2OfParent'})
            
            // Common Ancestor 0
            CREATE (person)-[:PARTNER_IN]->(spousePartnership:Partnership {id: '00000001-0000-0000-0000-000000000000'})
            CREATE (spouse)-[:PARTNER_IN]->(spousePartnership)
            CREATE (spousePartnership)-[:BEGAT]->(child)
            CREATE (child)-[:PARTNER_IN]->(childPartnership:Partnership {id: '00000002-0000-0000-0000-000000000000'})
            CREATE (childPartnership)-[:BEGAT]->(grandchild)
            CREATE (grandchild)-[:PARTNER_IN]->(grandchildPartnership:Partnership {id: '00000003-0000-0000-0000-000000000000'})
            CREATE (grandchildPartnership)-[:BEGAT]->(greatGrandchild)
            
            // Common Ancestor 1
            CREATE (parent)-[:PARTNER_IN]->(parentPartnership:Partnership {id: '00000005-0000-0000-0000-000000000000'})<-[:PARTNER_IN]-(otherParent)
            CREATE (parent)-[:PARTNER_IN]->(parentSecondMarriagePartnership:Partnership {id: '00000031-0000-0000-0000-000000000000'})<-[:PARTNER_IN]-(stepParent)
            CREATE (parentPartnership)-[:BEGAT]->(person)
            CREATE (parentPartnership)-[:BEGAT]->(sibling)
            CREATE (sibling)-[:PARTNER_IN]->(siblingPartnership:Partnership {id: '00000006-0000-0000-0000-000000000000'})
            CREATE (siblingPartnership)-[:BEGAT]->(nieceOrNephew)
            CREATE (nieceOrNephew)-[:PARTNER_IN]->(nieceOrNephewPartnership:Partnership {id: '00000007-0000-0000-0000-000000000000'})
            CREATE (nieceOrNephewPartnership)-[:BEGAT]->(greatNieceOrNephew)
            CREATE (greatNieceOrNephew)-[:PARTNER_IN]->(greatNieceOrNephewPartnership:Partnership {id: '00000008-0000-0000-0000-000000000000'})
            CREATE (greatNieceOrNephewPartnership)-[:BEGAT]->(greatGrandNieceOrNephew)
            
            // Common Ancestor 2
            CREATE (grandparent)-[:PARTNER_IN]->(grandparentPartnership:Partnership {id: '00000010-0000-0000-0000-000000000000'})
            CREATE (grandparentPartnership)-[:BEGAT]->(parent)
            CREATE (grandparentPartnership)-[:BEGAT]->(auntOrUncle)
            CREATE (auntOrUncle)-[:PARTNER_IN]->(auntOrUnclePartnership:Partnership {id: '00000011-0000-0000-0000-000000000000'})
            CREATE (auntOrUnclePartnership)-[:BEGAT]->(firstCousin)
            CREATE (firstCousin)-[:PARTNER_IN]->(firstCousinPartnership:Partnership {id: '00000012-0000-0000-0000-000000000000'})
            CREATE (firstCousinPartnership)-[:BEGAT]->(firstCousinOnceRemoved)
            CREATE (firstCousinOnceRemoved)-[:PARTNER_IN]->(firstCousinOnceRemovedPartnership:Partnership {id: '00000013-0000-0000-0000-000000000000'})
            CREATE (firstCousinOnceRemovedPartnership)-[:BEGAT]->(firstCousinTwiceRemoved)
            CREATE (firstCousinTwiceRemoved)-[:PARTNER_IN]->(firstCousinTwiceRemovedPartnership:Partnership {id: '00000014-0000-0000-0000-000000000000'})
            CREATE (firstCousinTwiceRemovedPartnership)-[:BEGAT]->(firstCousinThriceRemoved)
            
            // Common Ancestor 3
            CREATE (greatGrandparent)-[:PARTNER_IN]->(greatGrandparentPartnership:Partnership {id: '00000016-0000-0000-0000-000000000000'})
            CREATE (greatGrandparentPartnership)-[:BEGAT]->(grandparent)
            CREATE (greatGrandparentPartnership)-[:BEGAT]->(greatAuntOrUncle)
            CREATE (greatAuntOrUncle)-[:PARTNER_IN]->(greatAuntOrUnclePartnership:Partnership {id: '00000017-0000-0000-0000-000000000000'})
            CREATE (greatAuntOrUnclePartnership)-[:BEGAT]->(firstCousinOnceRemovedThroughGreatGrandparents)
            CREATE (firstCousinOnceRemovedThroughGreatGrandparents)-[:PARTNER_IN]->(firstCousinOnceRemovedThroughGreatGrandparentsPartnership:Partnership {id: '00000018-0000-0000-0000-000000000000'})
            CREATE (firstCousinOnceRemovedThroughGreatGrandparentsPartnership)-[:BEGAT]->(secondCousin)
            CREATE (secondCousin)-[:PARTNER_IN]->(secondCousinPartnership:Partnership {id: '00000019-0000-0000-0000-000000000000'})
            CREATE (secondCousinPartnership)-[:BEGAT]->(secondCousinOnceRemoved)
            CREATE (secondCousinOnceRemoved)-[:PARTNER_IN]->(secondCousinOnceRemovedPartnership:Partnership {id: '00000020-0000-0000-0000-000000000000'})
            CREATE (secondCousinOnceRemovedPartnership)-[:BEGAT]->(secondCousinTwiceRemoved)
            CREATE (secondCousinTwiceRemoved)-[:PARTNER_IN]->(secondCousinTwiceRemovedPartnership:Partnership {id: '00000021-0000-0000-0000-000000000000'})
            CREATE (secondCousinTwiceRemovedPartnership)-[:BEGAT]->(secondCousinThriceRemoved)
            
            // Common Ancestor 4
            CREATE (greatGreatGrandparent)-[:PARTNER_IN]->(greatGreatGrandparentPartnership:Partnership {id: '00000023-0000-0000-0000-000000000000'})
            CREATE (greatGreatGrandparentPartnership)-[:BEGAT]->(greatGrandparent)
            CREATE (greatGreatGrandparentPartnership)-[:BEGAT]->(greatGrandAuntOrUncle)
            CREATE (greatGrandAuntOrUncle)-[:PARTNER_IN]->(greatGrandAuntOrUnclePartnership:Partnership {id: '00000024-0000-0000-0000-000000000000'})
            CREATE (greatGrandAuntOrUnclePartnership)-[:BEGAT]->(firstCousinTwiceRemovedThroughGreatGreatGrandparents)
            CREATE (firstCousinTwiceRemovedThroughGreatGreatGrandparents)-[:PARTNER_IN]->(firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnership:Partnership {id: '00000025-0000-0000-0000-000000000000'})
            CREATE (firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnership)-[:BEGAT]->(secondCousinOnceRemovedThroughGreatGreatGrandparents)
            CREATE (secondCousinOnceRemovedThroughGreatGreatGrandparents)-[:PARTNER_IN]->(secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnership:Partnership {id: '00000026-0000-0000-0000-000000000000'})
            CREATE (secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnership)-[:BEGAT]->(thirdCousin)
            CREATE (thirdCousin)-[:PARTNER_IN]->(thirdCousinPartnership:Partnership {id: '00000027-0000-0000-0000-000000000000'})
            CREATE (thirdCousinPartnership)-[:BEGAT]->(thirdCousinOnceRemoved)
            CREATE (thirdCousinOnceRemoved)-[:PARTNER_IN]->(thirdCousinOnceRemovedPartnership:Partnership {id: '00000028-0000-0000-0000-000000000000'})
            CREATE (thirdCousinOnceRemovedPartnership)-[:BEGAT]->(thirdCousinTwiceRemoved)
            CREATE (thirdCousinTwiceRemoved)-[:PARTNER_IN]->(thirdCousinTwiceRemovedPartnership:Partnership {id: '00000029-0000-0000-0000-000000000000'})
            CREATE (thirdCousinTwiceRemovedPartnership)-[:BEGAT]->(thirdCousinThriceRemoved)
            """);
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
