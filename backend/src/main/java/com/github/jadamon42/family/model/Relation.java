package com.github.jadamon42.family.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Relation {
    int numberOfGenerationsToCommonAncestor;
    int numberOfGenerationsToOtherPerson;
    boolean isRelatedByBlood;
}
