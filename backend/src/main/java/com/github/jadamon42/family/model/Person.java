package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.List;

import static com.github.jadamon42.family.service.PatchHelper.set;

@Value
@With
@Builder
@AllArgsConstructor
@Node("Person")
public class Person {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    String firstName;
    String lastName;

    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.OUTGOING)
    List<Partnership> partnerships;

    public static Person fromRequest(PersonRequest request) {
        Person.PersonBuilder builder = Person.builder();
        set(builder, PersonBuilder::firstName, request.getFirstName());
        set(builder, PersonBuilder::lastName, request.getLastName());
        return builder.build();
    }
}
