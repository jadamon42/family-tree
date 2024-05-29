package com.github.jadamon42.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Set;
import java.util.UUID;

@Value
@With
@Builder
@AllArgsConstructor
public class Relationship {
    Set<UUID> pathIds;
    String relationshipLabel;
    String inverseRelationshipLabel;
}
