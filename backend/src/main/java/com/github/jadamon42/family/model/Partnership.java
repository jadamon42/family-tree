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

import java.time.LocalDate;
import java.util.List;

@Value
@With
@Builder
@AllArgsConstructor
@Jacksonized
@Node("Partnership")
public class Partnership {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String id;
    String type;
    LocalDate startDate;
    LocalDate endDate;

    @Relationship(value = "BEGAT", direction = Relationship.Direction.OUTGOING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<Person> children;

    @JsonCreator
    public static Partnership create(String type, LocalDate startDate, LocalDate endDate) {
        return Partnership.builder()
                     .type(type)
                     .startDate(startDate)
                     .endDate(endDate)
                     .build();
    }

    public static class PartnershipBuilder {
        @JsonAnySetter
        public Partnership.PartnershipBuilder unknown(String name, Object ignoredValue) {
            throw new IllegalArgumentException("Unknown property: " + name);
        }
    }
}
