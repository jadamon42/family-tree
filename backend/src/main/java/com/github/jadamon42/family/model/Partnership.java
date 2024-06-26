package com.github.jadamon42.family.model;

import com.github.jadamon42.family.util.UUIDStringConverter;
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

import static com.github.jadamon42.family.util.PatchHelper.set;

@Value
@With
@Builder
@AllArgsConstructor
@Node("Partnership")
public class Partnership {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    @ConvertWith(converter = UUIDStringConverter.class)
    UUID id;
    String type;
    LocalDate startDate;
    LocalDate endDate;

    @Relationship(value = "PARTNER_IN", direction = Relationship.Direction.INCOMING)
    @Builder.Default
    List<Person> partners = List.of();
    @Relationship(value = "BEGAT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    List<Person> children = List.of();

    public static Partnership fromRequest(PartnershipRequest request) {
        Partnership.PartnershipBuilder builder = Partnership.builder();
        set(builder, PartnershipBuilder::type, request.getType());
        set(builder, PartnershipBuilder::startDate, request.getStartDate());
        set(builder, PartnershipBuilder::endDate, request.getEndDate());
        return builder.build();
    }
}
