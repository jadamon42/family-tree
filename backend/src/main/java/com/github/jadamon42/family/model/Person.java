package com.github.jadamon42.family.model;

import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.List;

@Value
@With
@Node("Person")
public class Person {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    @Property
    String firstName;
    @Property
    String lastName;
    @Relationship("PARTNER_IN")
    List<Partnership> partnerships;
    @Relationship("PARENT_OF")
    List<Person> children;
}
