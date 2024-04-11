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

import java.time.LocalDate;
import java.util.List;

import static com.github.jadamon42.family.service.PatchHelper.set;

@Value
@With
@Builder
@AllArgsConstructor
@Node("Partnership")
public class Partnership {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;
    String type;
    LocalDate startDate;
    LocalDate endDate;

    @Relationship(value = "BEGAT", direction = Relationship.Direction.OUTGOING)
    List<Person> children;

    public static Partnership fromRequest(PartnershipRequest request) {
        Partnership.PartnershipBuilder builder = Partnership.builder();
        set(builder, PartnershipBuilder::type, request.getType());
        set(builder, PartnershipBuilder::startDate, request.getStartDate());
        set(builder, PartnershipBuilder::endDate, request.getEndDate());
        return builder.build();
    }
}
