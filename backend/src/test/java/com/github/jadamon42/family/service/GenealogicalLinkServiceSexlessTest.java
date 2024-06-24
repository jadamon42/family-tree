package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.Relationship;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
@Import(CustomCypherQueryExecutor.class)
@ActiveProfiles("test")
public class GenealogicalLinkServiceSexlessTest {
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
        assertThat(relationship.getPathIds()).contains(personId);
    }

    @Test
    void getGenealogicalLinkOfSpouse() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, spouseId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Spouse");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Spouse");
        assertThat(relationship.getPathIds()).contains(personId, spouseId);
    }

    @Test
    void getGenealogicalLinkOfChild() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, childId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Child");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Parent");
        assertThat(relationship.getPathIds()).contains(personId, childId);
    }

    @Test
    void getGenealogicalLinkOfGrandchild() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandchildId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandchild");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandparent");
        assertThat(relationship.getPathIds()).contains(personId, childId, grandchildId);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandchild() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandchildId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandchild");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandparent");
        assertThat(relationship.getPathIds()).contains(personId, childId, grandchildId, greatGrandchildId);
    }

    @Test
    void getGenealogicalLinkOfParent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, parentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Parent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Child");
        assertThat(relationship.getPathIds()).contains(personId, parentId);
    }

    @Test
    void getGenealogicalLinkOfSibling() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, siblingId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Sibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Sibling");
        assertThat(relationship.getPathIds()).contains(personId, siblingId);
    }

    @Test
    void getGenealogicalLinkOfNieceOrNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, nieceOrNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Nibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Pibling");
        assertThat(relationship.getPathIds()).contains(personId, nieceOrNephewId);
    }

    @Test
    void getGenealogicalLinkOfGreatNieceOrNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatNieceOrNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Nibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Pibling");
        assertThat(relationship.getPathIds()).contains(personId, nieceOrNephewId, greatNieceOrNephewId);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNieceOrNephew() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandNieceOrNephewId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Nibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Pibling");
        assertThat(relationship.getPathIds()).contains(personId, nieceOrNephewId, greatNieceOrNephewId, greatGrandNieceOrNephewId);
    }

    @Test
    void getGenealogicalLinkOfGrandparent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, grandparentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grandparent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grandchild");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId);
    }

    @Test
    void getGenealogicalLinkOfAuntOrUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, auntOrUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Pibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Nibling");
        assertThat(relationship.getPathIds()).contains(personId, auntOrUncleId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin");
        assertThat(relationship.getPathIds()).contains(personId, firstCousinId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getPathIds()).contains(personId, firstCousinOnceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getPathIds()).contains(personId, firstCousinTwiceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Thrice Removed");
        assertThat(relationship.getPathIds()).contains(personId, firstCousinThriceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandparent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandparentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grandparent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grandchild");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatGrandparentId);
    }

    @Test
    void getGenealogicalLinkOfGreatAuntOrUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatAuntOrUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Grand-Pibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Grand-Nibling");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatAuntOrUncleId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinOnceRemovedThroughGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Once Removed");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, firstCousinOnceRemovedThroughGreatGrandparentsId);
    }

    @Test
    void getGenealogicalLinkOfSecondCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin");
        assertThat(relationship.getPathIds()).contains(personId, secondCousinId);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getPathIds()).contains(personId, secondCousinOnceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Twice Removed");
        assertThat(relationship.getPathIds()).contains(personId, secondCousinTwiceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Thrice Removed");
        assertThat(relationship.getPathIds()).contains(personId, secondCousinThriceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandparent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGreatGrandparentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Great-Grandparent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Great-Grandchild");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatGrandparentId, greatGreatGrandparentId);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAuntOrUncle() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, greatGrandAuntOrUncleId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Great-Grand-Pibling");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Great-Grand-Nibling");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatGrandparentId, greatGrandAuntOrUncleId);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("1st Cousin Twice Removed");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatGrandparentId, firstCousinTwiceRemovedThroughGreatGreatGrandparentsId);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparents() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("2nd Cousin Once Removed");
        assertThat(relationship.getPathIds()).contains(personId, grandparentId, greatGrandparentId, secondCousinOnceRemovedThroughGreatGreatGrandparentsId);
    }

    @Test
    void getGenealogicalLinkOfThirdCousin() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin");
        assertThat(relationship.getPathIds()).contains(personId, thirdCousinId);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinOnceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Once Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Once Removed");
        assertThat(relationship.getPathIds()).contains(personId, thirdCousinOnceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinTwiceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Twice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Twice Removed");
        assertThat(relationship.getPathIds()).contains(personId, thirdCousinTwiceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemoved() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, thirdCousinThriceRemovedId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("3rd Cousin Thrice Removed");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("3rd Cousin Thrice Removed");
        assertThat(relationship.getPathIds()).contains(personId, thirdCousinThriceRemovedId);
    }

    @Test
    void getGenealogicalLinkOfOtherParent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, otherParentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Parent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Child");
        assertThat(relationship.getPathIds()).contains(personId, otherParentId);
    }

    @Test
    void getGenealogicalLinkOfStepParent() {
        Relationship relationship = genealogicalLinkService.getRelationship(personId, stepParentId).orElseThrow();
        assertThat(relationship.getRelationshipLabel()).isEqualTo("Step-Parent");
        assertThat(relationship.getInverseRelationshipLabel()).isEqualTo("Step-Child");
        assertThat(relationship.getPathIds()).contains(personId, stepParentId);
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
            CREATE (person)-[:PARTNER_IN]->(spousePartnership:Partnership {id: randomUUID()})
            CREATE (spouse)-[:PARTNER_IN]->(spousePartnership)
            CREATE (spousePartnership)-[:BEGAT]->(child)
            CREATE (child)-[:PARTNER_IN]->(childPartnership:Partnership {id: randomUUID()})
            CREATE (childPartnership)-[:BEGAT]->(grandchild)
            CREATE (grandchild)-[:PARTNER_IN]->(grandchildPartnership:Partnership {id: randomUUID()})
            CREATE (grandchildPartnership)-[:BEGAT]->(greatGrandchild)
            
            // Common Ancestor 1
            CREATE (parent)-[:PARTNER_IN]->(parentPartnership:Partnership {id: randomUUID()})<-[:PARTNER_IN]-(otherParent)
            CREATE (parent)-[:PARTNER_IN]->(parentSecondMarriagePartnership:Partnership {id: randomUUID()})<-[:PARTNER_IN]-(stepParent)
            CREATE (parentPartnership)-[:BEGAT]->(person)
            CREATE (parentPartnership)-[:BEGAT]->(sibling)
            CREATE (sibling)-[:PARTNER_IN]->(siblingPartnership:Partnership {id: randomUUID()})
            CREATE (siblingPartnership)-[:BEGAT]->(nieceOrNephew)
            CREATE (nieceOrNephew)-[:PARTNER_IN]->(nieceOrNephewPartnership:Partnership {id: randomUUID()})
            CREATE (nieceOrNephewPartnership)-[:BEGAT]->(greatNieceOrNephew)
            CREATE (greatNieceOrNephew)-[:PARTNER_IN]->(greatNieceOrNephewPartnership:Partnership {id: randomUUID()})
            CREATE (greatNieceOrNephewPartnership)-[:BEGAT]->(greatGrandNieceOrNephew)
            
            // Common Ancestor 2
            CREATE (grandparent)-[:PARTNER_IN]->(grandparentPartnership:Partnership {id: '00000010-0000-0000-0000-000000000000'})
            CREATE (grandparentPartnership)-[:BEGAT]->(parent)
            CREATE (grandparentPartnership)-[:BEGAT]->(auntOrUncle)
            CREATE (auntOrUncle)-[:PARTNER_IN]->(auntOrUnclePartnership:Partnership {id: randomUUID()})
            CREATE (auntOrUnclePartnership)-[:BEGAT]->(firstCousin)
            CREATE (firstCousin)-[:PARTNER_IN]->(firstCousinPartnership:Partnership {id: randomUUID()})
            CREATE (firstCousinPartnership)-[:BEGAT]->(firstCousinOnceRemoved)
            CREATE (firstCousinOnceRemoved)-[:PARTNER_IN]->(firstCousinOnceRemovedPartnership:Partnership {id: randomUUID()})
            CREATE (firstCousinOnceRemovedPartnership)-[:BEGAT]->(firstCousinTwiceRemoved)
            CREATE (firstCousinTwiceRemoved)-[:PARTNER_IN]->(firstCousinTwiceRemovedPartnership:Partnership {id: randomUUID()})
            CREATE (firstCousinTwiceRemovedPartnership)-[:BEGAT]->(firstCousinThriceRemoved)
            
            // Common Ancestor 3
            CREATE (greatGrandparent)-[:PARTNER_IN]->(greatGrandparentPartnership:Partnership {id: randomUUID()})
            CREATE (greatGrandparentPartnership)-[:BEGAT]->(grandparent)
            CREATE (greatGrandparentPartnership)-[:BEGAT]->(greatAuntOrUncle)
            CREATE (greatAuntOrUncle)-[:PARTNER_IN]->(greatAuntOrUnclePartnership:Partnership {id: randomUUID()})
            CREATE (greatAuntOrUnclePartnership)-[:BEGAT]->(firstCousinOnceRemovedThroughGreatGrandparents)
            CREATE (firstCousinOnceRemovedThroughGreatGrandparents)-[:PARTNER_IN]->(firstCousinOnceRemovedThroughGreatGrandparentsPartnership:Partnership {id: randomUUID()})
            CREATE (firstCousinOnceRemovedThroughGreatGrandparentsPartnership)-[:BEGAT]->(secondCousin)
            CREATE (secondCousin)-[:PARTNER_IN]->(secondCousinPartnership:Partnership {id: randomUUID()})
            CREATE (secondCousinPartnership)-[:BEGAT]->(secondCousinOnceRemoved)
            CREATE (secondCousinOnceRemoved)-[:PARTNER_IN]->(secondCousinOnceRemovedPartnership:Partnership {id: '00000020-0000-0000-0000-000000000000'})
            CREATE (secondCousinOnceRemovedPartnership)-[:BEGAT]->(secondCousinTwiceRemoved)
            CREATE (secondCousinTwiceRemoved)-[:PARTNER_IN]->(secondCousinTwiceRemovedPartnership:Partnership {id: randomUUID()})
            CREATE (secondCousinTwiceRemovedPartnership)-[:BEGAT]->(secondCousinThriceRemoved)
            
            // Common Ancestor 4
            CREATE (greatGreatGrandparent)-[:PARTNER_IN]->(greatGreatGrandparentPartnership:Partnership {id: randomUUID()})
            CREATE (greatGreatGrandparentPartnership)-[:BEGAT]->(greatGrandparent)
            CREATE (greatGreatGrandparentPartnership)-[:BEGAT]->(greatGrandAuntOrUncle)
            CREATE (greatGrandAuntOrUncle)-[:PARTNER_IN]->(greatGrandAuntOrUnclePartnership:Partnership {id: randomUUID()})
            CREATE (greatGrandAuntOrUnclePartnership)-[:BEGAT]->(firstCousinTwiceRemovedThroughGreatGreatGrandparents)
            CREATE (firstCousinTwiceRemovedThroughGreatGreatGrandparents)-[:PARTNER_IN]->(firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnership:Partnership {id: randomUUID()})
            CREATE (firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnership)-[:BEGAT]->(secondCousinOnceRemovedThroughGreatGreatGrandparents)
            CREATE (secondCousinOnceRemovedThroughGreatGreatGrandparents)-[:PARTNER_IN]->(secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnership:Partnership {id: randomUUID()})
            CREATE (secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnership)-[:BEGAT]->(thirdCousin)
            CREATE (thirdCousin)-[:PARTNER_IN]->(thirdCousinPartnership:Partnership {id: randomUUID()})
            CREATE (thirdCousinPartnership)-[:BEGAT]->(thirdCousinOnceRemoved)
            CREATE (thirdCousinOnceRemoved)-[:PARTNER_IN]->(thirdCousinOnceRemovedPartnership:Partnership {id: randomUUID()})
            CREATE (thirdCousinOnceRemovedPartnership)-[:BEGAT]->(thirdCousinTwiceRemoved)
            CREATE (thirdCousinTwiceRemoved)-[:PARTNER_IN]->(thirdCousinTwiceRemovedPartnership:Partnership {id: randomUUID()})
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
