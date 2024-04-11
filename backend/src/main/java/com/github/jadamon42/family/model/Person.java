package com.github.jadamon42.family.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
@With
@Builder
@AllArgsConstructor
@Jacksonized
@Node("Person")
public class Person {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String id;
    String firstName;
    String lastName;

    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.OUTGOING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<Partnership> partnerships;

    @JsonCreator
    public static Person create(String firstName, String lastName) {
        return Person.builder()
                     .firstName(firstName)
                     .lastName(lastName)
                     .build();
    }

    public static class PersonBuilder {
        @JsonAnySetter
        public PersonBuilder unknown(String name, Object ignoredValue) {
            throw new IllegalArgumentException("Unknown property: " + name);
        }
    }
}
