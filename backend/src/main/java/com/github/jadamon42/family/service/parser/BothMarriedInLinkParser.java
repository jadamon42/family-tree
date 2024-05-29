package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;

import java.util.Optional;

public class BothMarriedInLinkParser extends GenealogicalLinkParser {
    public BothMarriedInLinkParser(CustomCypherQueryExecutor customCypherQueryExecutor) {
        super(customCypherQueryExecutor);
    }

    public String getLabel(GenealogicalLink link) {
        String retval;
        Relation relation = link.getRelationFromPerspectiveOfPersonFrom();
        int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
        int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();
        int difference = numberOfGenerationsToCommonAncestor - numberOfGenerationsToOtherPerson;

        if (difference == 0) {
            retval = switch (numberOfGenerationsToCommonAncestor) {
                case 0 -> "Self" ;
                case 1 ->  "%s-in-Law".formatted(getParentLabel(link));
                default -> "%s%s-in-Law".formatted(getGreatPrefix(numberOfGenerationsToCommonAncestor - 2), getGrandParentLabel(link));
            };
        } else {
            retval = switch (numberOfGenerationsToCommonAncestor) {
                case 0 -> switch (numberOfGenerationsToOtherPerson) {
                    case -1 -> "Step-%s".formatted(getChildLabel(link));
                    default -> "Step-%s%s".formatted(getGreatPrefix(Math.abs(numberOfGenerationsToOtherPerson) - 2), getGrandChildLabel(link));
                };
                case 1 -> switch (numberOfGenerationsToOtherPerson) {
                    case 0 -> getSpouseOrSiblingInLawLabel(link);
                    default -> "Step-%s".formatted(getChildLabel(link));
                };
                default ->  {
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            yield "Step-%s-in-Law".formatted(getParentLabel(link));
                        } else if (numberOfGreats == 1) {
                            yield "Step-%s-in-Law".formatted(getGrandParentLabel(link));
                        } else {
                            Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                            if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                yield "%sGrand-%s-in-Law".formatted(getGreatPrefix(numberOfGreats - 1), getPiblingLabel(link));
                            } else {
                                yield "Step-%s%s-in-Law".formatted(getGreatPrefix(numberOfGreats - 1), getGrandParentLabel(link));
                            }
                        }
                    }
                    yield "Step-%s".formatted(getSiblingLabel(link));
                }
            };
        }
        return retval;
    }
}
