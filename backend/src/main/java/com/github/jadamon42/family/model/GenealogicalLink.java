package com.github.jadamon42.family.model;

import com.github.jadamon42.family.exception.PersonNotFoundException;
import com.github.jadamon42.family.util.SexConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class GenealogicalLink {
    private static SexConverter sexConverter = new SexConverter();

    UUID sharedAncestralPartnershipId;
    Collection<UUID> commonAncestorIds;
    UUID personFromId;
    UUID personToId;
    Sex personFromSex;
    Sex personToSex;
    Boolean personFromMarriedIn;
    Boolean personToMarriedIn;
    @Getter(AccessLevel.NONE)
    Relation relationFromPerspectiveOfPerson1;
    @Getter(AccessLevel.NONE)
    Relation relationFromPerspectiveOfPerson2;

    public Relation getRelationFromPerspectiveOfPerson(UUID personId) {
        if (personId.equals(personFromId)) {
            return relationFromPerspectiveOfPerson1;
        } else if (personId.equals(personToId)) {
            return relationFromPerspectiveOfPerson2;
        } else {
            throw new PersonNotFoundException(personId);
        }
    }

    public static GenealogicalLink fromRecord(TypeSystem ignored, Record record) {
        return GenealogicalLink.builder()
                               .personFromId(getUUID(record.get("person1Id")))
                               .personToId(getUUID(record.get("person2Id")))
                               .personFromSex(sexConverter.convert(getString(record.get("person1Sex"))))
                               .personToSex(sexConverter.convert(getString(record.get("person2Sex"))))
                               .personFromMarriedIn(record.get("person1MarriedIn").asBoolean())
                               .personToMarriedIn(record.get("person2MarriedIn").asBoolean())
                               .sharedAncestralPartnershipId(getUUID(record.get("sharedAncestralPartnershipId")))
                               .commonAncestorIds(getCommonAncestorIds(record))
                               .relationFromPerspectiveOfPerson1(buildRelation(record, "person1"))
                               .relationFromPerspectiveOfPerson2(buildRelation(record, "person2"))
                               .build();
    }

    private static List<UUID> getCommonAncestorIds(Record record) {
        return Stream.of(
                        getUUID(record.get("commonAncestorId")),
                        getUUID(record.get("otherCommonAncestorId")))
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    private static String getString(org.neo4j.driver.Value value) {
        return value.asString(null);
    }

    private static UUID getUUID(org.neo4j.driver.Value commonAncestorId) {
        return commonAncestorId.isNull() ? null : UUID.fromString(commonAncestorId.asString());
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
