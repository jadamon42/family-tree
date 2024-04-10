package com.github.jadamon42.family.model;

import lombok.Value;
import lombok.With;
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
@Node("Person")
public class Person {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    String firstName;
    String lastName;

    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.OUTGOING)
    List<Partnership> partnerships;
    @Relationship(value = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    List<Person> children;

    public Person withPartnership(Partnership partnership) {
        List<Partnership> updatedPartnerships = partnerships != null ? new ArrayList<>(partnerships) : new ArrayList<>();

        updatedPartnerships = updatedPartnerships.stream()
                                                 .filter(p -> p.getId() == null || !p.getId().equals(partnership.getId()))
                                                 .collect(Collectors.toList());
        updatedPartnerships.add(partnership);

        return this.withPartnerships(updatedPartnerships);
    }

    public Person withChild(Person child) {
        List<Person> updatedChildren = children != null ? new ArrayList<>(children) : new ArrayList<>();

        updatedChildren = updatedChildren.stream()
                                         .filter(c -> child.getId() == null || !c.getId().equals(child.getId()))
                                         .collect(Collectors.toList());
        updatedChildren.add(child);

        return this.withChildren(updatedChildren);
    }
}
