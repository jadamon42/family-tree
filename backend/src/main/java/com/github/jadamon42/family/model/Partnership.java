package com.github.jadamon42.family.model;

import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;

@Value
@With
@Node("Partnership")
public class Partnership {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    String type;
    LocalDate startDate;
    LocalDate endDate;
}
