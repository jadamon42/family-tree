package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.github.jadamon42.family.service.PatchHelper.set;

@Value
@With
@Builder
@AllArgsConstructor
@Node("Person")
public class Person {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    @ConvertWith(converter = UUIDStringConverter.class)
    UUID id;
    String firstName;
    String lastName;
    String sex;
    LocalDate birthDate;
    LocalDate deathDate;

    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.OUTGOING)
    List<Partnership> partnerships;

    public static Person fromRequest(PersonRequest request) {
        Person.PersonBuilder builder = Person.builder();
        set(builder, PersonBuilder::firstName, request.getFirstName());
        set(builder, PersonBuilder::lastName, request.getLastName());
        set(builder, PersonBuilder::sex, request.getSex());
        set(builder, PersonBuilder::birthDate, request.getBirthDate());
        set(builder, PersonBuilder::deathDate, request.getDeathDate());
        return builder.build();
    }
}
