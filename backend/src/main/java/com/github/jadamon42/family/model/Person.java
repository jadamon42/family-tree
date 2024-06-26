package com.github.jadamon42.family.model;

import com.github.jadamon42.family.util.SexConverter;
import com.github.jadamon42.family.util.UUIDStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;
import java.util.UUID;

import static com.github.jadamon42.family.util.PatchHelper.set;

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
    String middleName;
    String lastName;
    LocalDate birthDate;
    LocalDate deathDate;
    @ConvertWith(converter = SexConverter.class)
    @Builder.Default
    Sex sex = Sex.UNKNOWN;

    public static Person fromRequest(PersonRequest request) {
        Person.PersonBuilder builder = Person.builder();
        set(builder, PersonBuilder::firstName, request.getFirstName());
        set(builder, PersonBuilder::middleName, request.getMiddleName());
        set(builder, PersonBuilder::lastName, request.getLastName());
        set(builder, PersonBuilder::sex, request.getSex());
        set(builder, PersonBuilder::birthDate, request.getBirthDate());
        set(builder, PersonBuilder::deathDate, request.getDeathDate());
        return builder.build();
    }
}
