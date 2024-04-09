package com.github.jadamon42.family.model;

import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;

@Value
@With
@Node("Partnership")
public class Partnership {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    @Property
    String type;
    @Property
    LocalDate startDate;
    @Property
    LocalDate endDate;
//    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.INCOMING)
//    Person partner1;
//    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.INCOMING)
//    Person partner2;
}
