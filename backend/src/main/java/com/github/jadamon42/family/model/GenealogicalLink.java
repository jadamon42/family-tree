package com.github.jadamon42.family.model;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class GenealogicalLink {
    @Getter(AccessLevel.NONE)
    String person1Id;
    @Getter(AccessLevel.NONE)
    String person2Id;
    @Getter(AccessLevel.NONE)
    String person1Sex;
    @Getter(AccessLevel.NONE)
    String person2Sex;
    @Getter(AccessLevel.NONE)
    Relation relationFromPerspectiveOfPerson1;
    @Getter(AccessLevel.NONE)
    Relation relationFromPerspectiveOfPerson2;

    String sharedAncestralPartnershipId;
    Collection<String> commonAncestorIds;

    public Relation getRelationFromPerspectiveOfPerson(UUID personId) {
        if (personId.toString().equals(person1Id)) {
            return relationFromPerspectiveOfPerson1;
        } else if (personId.toString().equals(person2Id)) {
            return relationFromPerspectiveOfPerson2;
        } else {
            throw new PersonNotFoundException(personId);
        }
    }

    public String getSexOfPerson(UUID personId) throws PersonNotFoundException {
        if (personId.toString().equals(person1Id)) {
            return person1Sex;
        } else if (personId.toString().equals(person2Id)) {
            return person2Sex;
        } else {
            throw new PersonNotFoundException(personId);
        }
    }

    public static GenealogicalLink fromRecord(TypeSystem ignored, Record record) {
        return GenealogicalLink.builder()
                               .person1Id(getString(record.get("person1Id")))
                               .person2Id(getString(record.get("person2Id")))
                               .person1Sex(getString(record.get("person1Sex")))
                               .person2Sex(getString(record.get("person2Sex")))
                               .sharedAncestralPartnershipId(getString(record.get("sharedAncestralPartnershipId")))
                               .commonAncestorIds(getCommonAncestorIds(record))
                               .relationFromPerspectiveOfPerson1(buildRelation(record, "person1"))
                               .relationFromPerspectiveOfPerson2(buildRelation(record, "person2"))
                               .build();
    }

    private static List<String> getCommonAncestorIds(Record record) {
        return Stream.of(
                        getString(record.get("commonAncestorId")),
                        getString(record.get("otherCommonAncestorId")))
                     .filter(s -> s != null && !s.isEmpty())
                     .collect(Collectors.toList());
    }

    private static String getString(org.neo4j.driver.Value value) {
        return value.asString(null);
    }


    private static Relation buildRelation(Record record, String personPrefix) {
        String otherPrefix = personPrefix.equals("person1") ? "person2" : "person1";
        int numberOfGenerationsToCommonAncestor = record.get(personPrefix + "NumberOfPersonNodesToCommonAncestor").asInt();
        int otherNumberOfGenerationsToCommonAncestor = record.get(otherPrefix + "NumberOfPersonNodesToCommonAncestor").asInt();
        boolean marriedIn = record.get(personPrefix + "MarriedIn").asBoolean();
        boolean otherMarriedIn = record.get(otherPrefix + "MarriedIn").asBoolean();

        // If the person is married in, their spouse counts as 1 person node on the path to the common ancestor.
        // The spousal step is not a generational step, so we subtract 1
        if (marriedIn) {
            numberOfGenerationsToCommonAncestor -= 1;
        }
        if (otherMarriedIn) {
            otherNumberOfGenerationsToCommonAncestor -= 1;
        }

        return Relation.builder()
                       .numberOfGenerationsToCommonAncestor(numberOfGenerationsToCommonAncestor)
                       .numberOfGenerationsToOtherPerson(numberOfGenerationsToCommonAncestor - otherNumberOfGenerationsToCommonAncestor)
                       .isBloodRelation(!marriedIn && !otherMarriedIn)
                       .build();
    }
}
