package com.github.jadamon42.family.model;

import lombok.Builder;
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
    String person1Id;
    String person2Id;
    String sharedAncestralPartnershipId;
    Collection<String> commonAncestorIds;
    Relation relationFromPerspectiveOfPerson1;
    Relation relationFromPerspectiveOfPerson2;

    public Relation getRelationFromPerspectiveOfPerson(UUID personId) {
        if (personId.toString().equals(person1Id)) {
            return relationFromPerspectiveOfPerson1;
        } else if (personId.toString().equals(person2Id)) {
            return relationFromPerspectiveOfPerson2;
        } else {
            throw new IllegalArgumentException("Person with ID " + personId + " is not part of this genealogical link.");
        }
    }

    public static GenealogicalLink selfLink(String personId) {
        return GenealogicalLink.builder()
                               .person1Id(personId)
                               .person2Id(personId)
                               .sharedAncestralPartnershipId(null)
                               .commonAncestorIds(List.of(personId))
                               .relationFromPerspectiveOfPerson1(Relation.builder().numberOfGenerationsToCommonAncestor(0).numberOfGenerationsToOtherPerson(0).isRelatedByBlood(true).build())
                               .relationFromPerspectiveOfPerson2(Relation.builder().numberOfGenerationsToCommonAncestor(0).numberOfGenerationsToOtherPerson(0).isRelatedByBlood(true).build())
                               .build();
    }

    public static GenealogicalLink fromRecord(TypeSystem ignored, Record record) {
        return GenealogicalLink.builder()
                               .person1Id(getString(record.get("person1Id")))
                               .person2Id(getString(record.get("person2Id")))
                               .sharedAncestralPartnershipId(getString(record.get("sharedAncestralPartnershipId")))
                               .commonAncestorIds(Stream.of(
                                                                getString(record.get("commonAncestorId")),
                                                                getString(record.get("otherCommonAncestorId")))
                                                        .filter(s -> s != null && !s.isEmpty())
                                                        .collect(Collectors.toList()))
                               .relationFromPerspectiveOfPerson1(buildRelation(record, "p1"))
                               .relationFromPerspectiveOfPerson2(buildRelation(record, "p2"))
                               .build();
    }

    private static String getString(org.neo4j.driver.Value value) {
        return value.isNull() ? null : value.asString();
    }


    private static Relation buildRelation(Record record, String personPrefix) {
        String otherPrefix = personPrefix.equals("p1") ? "p2" : "p1";
        int numberOfGenerationsToCommonAncestor = record.get(personPrefix + "NumberOfPersonNodesToCommonAncestor").asInt();
        int otherNumberOfGenerationsToCommonAncestor = record.get(otherPrefix + "NumberOfPersonNodesToCommonAncestor").asInt();
        boolean marriedIn = record.get(personPrefix + "MarriedIn").asBoolean();
        boolean otherMarriedIn = record.get(otherPrefix + "MarriedIn").asBoolean();

        if (marriedIn) {
            numberOfGenerationsToCommonAncestor -= 1;
        }

        if (otherMarriedIn) {
            otherNumberOfGenerationsToCommonAncestor -= 1;
        }

        return Relation.builder()
                       .numberOfGenerationsToCommonAncestor(numberOfGenerationsToCommonAncestor)
                       .numberOfGenerationsToOtherPerson(numberOfGenerationsToCommonAncestor - otherNumberOfGenerationsToCommonAncestor)
                       .isRelatedByBlood(!marriedIn && !otherMarriedIn)
                       .build();
    }
}
