package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import com.github.jadamon42.family.repository.PartnershipRepository;
import com.github.jadamon42.family.repository.PersonRepository;
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
public class GenealogicalLinkPersonServiceTest {
    @Test
    void getGenealogicalLinkOfNonExistentPerson() {
        Optional<GenealogicalLink> link = personService.getGenealogicalLink(UUID.randomUUID(), UUID.fromString(personId));

        assertThat(link).isEmpty();
    }

    @Test
    void getGenealogicalLinkOfUnrelatedPerson() {
        Optional<GenealogicalLink> link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(unrelatedPersonId));

        assertThat(link).isEmpty();
    }
    
    @Test
    void getGenealogicalLinkOfSelf() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(personId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(personId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(0);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
    }

//    @Test
//    void getGenealogicalLinkOfSpouse() {
//        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(spouseId)).orElseThrow();
//        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));
//
//        assertThat(link.getCommonAncestorIds()).isNull();
//        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(spousePartnershipId);
//        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(0);
//        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
//    }

    @Test
    void getGenealogicalLinkOfChild() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(childId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(personId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(0);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-1);
    }

    @Test
    void getGenealogicalLinkOfGrandchild() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(grandchildId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(personId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(0);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-2);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandchild() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatGrandchildId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(personId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(0);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-3);
    }

    @Test
    void getGenealogicalLinkOfParent() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(parentId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(parentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
    }

    @Test
    void getGenealogicalLinkOfSibling() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(siblingId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(parentId, otherParentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(parentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
    }

    @Test
    void getGenealogicalLinkOfNieceOrNephew() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(nieceOrNephewId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(parentId, otherParentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(parentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-1);
    }

    @Test
    void getGenealogicalLinkOfGreatNieceOrNephew() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatNieceOrNephewId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(parentId, otherParentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(parentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-2);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandNieceOrNephew() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatGrandNieceOrNephewId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(parentId, otherParentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(parentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-3);
    }

    @Test
    void getGenealogicalLinkOfGrandparent() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(grandparentId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(2);
    }

    @Test
    void getGenealogicalLinkOfAuntOrUncle() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(auntOrUncleId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(grandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
    }

    @Test
    void getGenealogicalLinkOfFirstCousin() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(grandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinOnceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(grandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-1);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinTwiceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(grandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-2);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinThriceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinThriceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(grandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(grandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(2);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-3);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandparent() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatGrandparentId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(3);
    }

    @Test
    void getGenealogicalLinkOfGreatAuntOrUncle() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatAuntOrUncleId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(2);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinOnceRemovedThroughGreatGrandparents() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinOnceRemovedThroughGreatGrandparentsId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
    }

    @Test
    void getGenealogicalLinkOfSecondCousin() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(secondCousinId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(secondCousinOnceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-1);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinTwiceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(secondCousinTwiceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-2);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinThriceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(secondCousinThriceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(3);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-3);
    }

    @Test
    void getGenealogicalLinkOfGreatGreatGrandparent() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatGreatGrandparentId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(4);
    }

    @Test
    void getGenealogicalLinkOfGreatGrandAuntOrUncle() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(greatGrandAuntOrUncleId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(3);
    }

    @Test
    void getGenealogicalLinkOfFirstCousinTwiceRemovedThroughGreatGreatGrandparents() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(firstCousinTwiceRemovedThroughGreatGreatGrandparentsId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(2);
    }

    @Test
    void getGenealogicalLinkOfSecondCousinOnceRemovedThroughGreatGreatGrandparents() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(secondCousinOnceRemovedThroughGreatGreatGrandparentsId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
    }

    @Test
    void getGenealogicalLinkOfThirdCousin() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(thirdCousinId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(0);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinOnceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(thirdCousinOnceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-1);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinTwiceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(thirdCousinTwiceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-2);
    }

    @Test
    void getGenealogicalLinkOfThirdCousinThriceRemoved() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(thirdCousinThriceRemovedId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(greatGreatGrandparentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isEqualTo(greatGreatGrandparentPartnershipId);
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(4);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(-3);
    }

    @Test
    void getGenealogicalLinkOfOtherParent() {
        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(otherParentId)).orElseThrow();
        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));

        assertThat(link.getCommonAncestorIds()).containsExactly(otherParentId);
        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
    }

//    @Test
//    void getGenealogicalLinkOfStepParent() {
//        GenealogicalLink link = personService.getGenealogicalLink(UUID.fromString(personId), UUID.fromString(stepParentId)).orElseThrow();
//        Relation relation = link.getRelationFromPerspectiveOfPerson(UUID.fromString(personId));
//
//        assertThat(link.getCommonAncestorIds()).isNull();
//        assertThat(link.getCommonAncestorsPartnershipId()).isNull();
//        assertThat(relation.getNumberOfGenerationsToCommonAncestor()).isEqualTo(1);
//        assertThat(relation.getNumberOfGenerationsToOtherPerson()).isEqualTo(1);
//    }

    @Test
    void getGenealogicalLinkOfHalfSiblings() {

    }

    @Test
    void getGenealogicalLinkOfHalfCousins() {

    }

    @Test
    void getGenealogicalLinkOfHalfAuntOrUncleAndNieceOrNephew() {

    }

    @Test
    void getGenealogicalLinkOfHalfGrandparentAndGrandchild() {

    }

    private static Neo4j embeddedDatabaseServer;
    private static Driver neo4jDriver;
    private final PersonService personService;
//    private final PartnershipRepository partnershipRepository;
//    private final CustomCypherQueryExecutor executor;
    
    private final String personId = "00000000-0000-0000-0000-000000000000";
    private final String spouseId = "00000000-0000-0000-0000-000000000001";
    private final String childId = "00000000-0000-0000-0000-000000000002";
    private final String grandchildId = "00000000-0000-0000-0000-000000000003";
    private final String greatGrandchildId = "00000000-0000-0000-0000-000000000004";
    private final String parentId = "00000000-0000-0000-0000-000000000005";
    private final String siblingId = "00000000-0000-0000-0000-000000000006";
    private final String nieceOrNephewId = "00000000-0000-0000-0000-000000000007";
    private final String greatNieceOrNephewId = "00000000-0000-0000-0000-000000000008";
    private final String greatGrandNieceOrNephewId = "00000000-0000-0000-0000-000000000009";
    private final String grandparentId = "00000000-0000-0000-0000-000000000010";
    private final String auntOrUncleId = "00000000-0000-0000-0000-000000000011";
    private final String firstCousinId = "00000000-0000-0000-0000-000000000012";
    private final String firstCousinOnceRemovedId = "00000000-0000-0000-0000-000000000013";
    private final String firstCousinTwiceRemovedId = "00000000-0000-0000-0000-000000000014";
    private final String firstCousinThriceRemovedId = "00000000-0000-0000-0000-000000000015";
    private final String greatGrandparentId = "00000000-0000-0000-0000-000000000016";
    private final String greatAuntOrUncleId = "00000000-0000-0000-0000-000000000017";
    private final String firstCousinOnceRemovedThroughGreatGrandparentsId = "00000000-0000-0000-0000-000000000018";
    private final String secondCousinId = "00000000-0000-0000-0000-000000000019";
    private final String secondCousinOnceRemovedId = "00000000-0000-0000-0000-000000000020";
    private final String secondCousinTwiceRemovedId = "00000000-0000-0000-0000-000000000021";
    private final String secondCousinThriceRemovedId = "00000000-0000-0000-0000-000000000022";
    private final String greatGreatGrandparentId = "00000000-0000-0000-0000-000000000023";
    private final String greatGrandAuntOrUncleId = "00000000-0000-0000-0000-000000000024";
    private final String firstCousinTwiceRemovedThroughGreatGreatGrandparentsId = "00000000-0000-0000-0000-000000000025";
    private final String secondCousinOnceRemovedThroughGreatGreatGrandparentsId = "00000000-0000-0000-0000-000000000026";
    private final String thirdCousinId = "00000000-0000-0000-0000-000000000027";
    private final String thirdCousinOnceRemovedId = "00000000-0000-0000-0000-000000000028";
    private final String thirdCousinTwiceRemovedId = "00000000-0000-0000-0000-000000000029";
    private final String thirdCousinThriceRemovedId = "00000000-0000-0000-0000-000000000030";
    private final String unrelatedPersonId = "00000000-0000-0000-0000-000000000031";
    private final String otherParentId = "00000000-0000-0000-0000-000000000032";
    private final String stepParentId = "00000000-0000-0000-0000-000000000033";

    private final String spousePartnershipId = "00000001-0000-0000-0000-000000000000";
    private final String childPartnershipId = "00000002-0000-0000-0000-000000000000";
    private final String grandchildPartnershipId = "00000003-0000-0000-0000-000000000000";
    private final String greatGrandchildPartnershipId = "00000004-0000-0000-0000-000000000000";
    private final String parentPartnershipId = "00000005-0000-0000-0000-000000000000";
    private final String siblingPartnershipId = "00000006-0000-0000-0000-000000000000";
    private final String nieceOrNephewPartnershipId = "00000007-0000-0000-0000-000000000000";
    private final String greatNieceOrNephewPartnershipId = "00000008-0000-0000-0000-000000000000";
    private final String greatGrandNieceOrNephewPartnershipId = "00000009-0000-0000-0000-000000000000";
    private final String grandparentPartnershipId = "00000010-0000-0000-0000-000000000000";
    private final String auntOrUnclePartnershipId = "00000011-0000-0000-0000-000000000000";
    private final String firstCousinPartnershipId = "00000012-0000-0000-0000-000000000000";
    private final String firstCousinOnceRemovedPartnershipId = "00000013-0000-0000-0000-000000000000";
    private final String firstCousinTwiceRemovedPartnershipId = "00000014-0000-0000-0000-000000000000";
    private final String firstCousinThriceRemovedPartnershipId = "00000015-0000-0000-0000-000000000000";
    private final String greatGrandparentPartnershipId = "00000016-0000-0000-0000-000000000000";
    private final String greatAuntOrUnclePartnershipId = "00000017-0000-0000-0000-000000000000";
    private final String firstCousinOnceRemovedThroughGreatGrandparentsPartnershipId = "00000018-0000-0000-0000-000000000000";
    private final String secondCousinPartnershipId = "00000019-0000-0000-0000-000000000000";
    private final String secondCousinOnceRemovedPartnershipId = "00000020-0000-0000-0000-000000000000";
    private final String secondCousinTwiceRemovedPartnershipId = "00000021-0000-0000-0000-000000000000";
    private final String secondCousinThriceRemovedPartnershipId = "00000022-0000-0000-0000-000000000000";
    private final String greatGreatGrandparentPartnershipId = "00000023-0000-0000-0000-000000000000";
    private final String greatGrandAuntOrUnclePartnershipId = "00000024-0000-0000-0000-000000000000";
    private final String firstCousinTwiceRemovedThroughGreatGreatGrandparentsPartnershipId = "00000025-0000-0000-0000-000000000000";
    private final String secondCousinOnceRemovedThroughGreatGreatGrandparentsPartnershipId = "00000026-0000-0000-0000-000000000000";
    private final String thirdCousinPartnershipId = "00000027-0000-0000-0000-000000000000";
    private final String thirdCousinOnceRemovedPartnershipId = "00000028-0000-0000-0000-000000000000";
    private final String thirdCousinTwiceRemovedPartnershipId = "00000029-0000-0000-0000-000000000000";
    private final String thirdCousinThriceRemovedPartnershipId = "00000030-0000-0000-0000-000000000000";
    private final String parentSecondMarriagePartnershipId = "00000031-0000-0000-0000-000000000000";

    @Autowired
    GenealogicalLinkPersonServiceTest(PersonRepository personRepository,
                                      PartnershipRepository partnershipRepository,
                                      CustomCypherQueryExecutor customCypherQueryExecutor) {
//        this.partnershipRepository = partnershipRepository;
        this.personService = new PersonService(personRepository, partnershipRepository, customCypherQueryExecutor);
//        this.executor = customCypherQueryExecutor;
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
            CREATE (unrelated: Person {id: '00000000-0000-0000-0000-000000000031', firstName: 'Unrelated', lastName: 'Unrelated'})
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
