package com.github.jadamon42.family.model;

import lombok.Builder;
import lombok.Value;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Value
@Builder
public class GenealogicalLink {
    String person1Id;
    String person2Id;
    String commonAncestorsPartnershipId;
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

    public static GenealogicalLink fromRecord(TypeSystem ignoredTypeSystem, Record record) {
        return GenealogicalLink.builder()
            .person1Id(getString(record.get("person1Id")))
            .person2Id(getString(record.get("person2Id")))
            .commonAncestorsPartnershipId(getString(record.get("commonAncestorsPartnershipId")))
            .commonAncestorIds(Stream.of(
                    getString(record.get("commonAncestorId")),
                    getString(record.get("otherCommonAncestorId")))
                 .filter(Objects::nonNull)
                 .collect(Collectors.toList()))
            .relationFromPerspectiveOfPerson1(Relation.builder()
                .numberOfGenerationsToCommonAncestor(record.get("numberOfGenerationsToCommonAncestorForP1").asInt())
                .numberOfGenerationsToOtherPerson(record.get("numberOfGenerationsToCommonAncestorForP1").asInt() - record.get("numberOfGenerationsToCommonAncestorForP2").asInt())
                .build())
            .relationFromPerspectiveOfPerson2(Relation.builder()
                .numberOfGenerationsToOtherPerson(record.get("numberOfGenerationsToCommonAncestorForP2").asInt())
                .numberOfGenerationsToCommonAncestor(record.get("numberOfGenerationsToCommonAncestorForP2").asInt() - record.get("numberOfGenerationsToCommonAncestorForP1").asInt())
                .build())
            .build();
    }

    private static String getString(org.neo4j.driver.Value value) {
        return value.isNull() ? null : value.asString();
    }
}
