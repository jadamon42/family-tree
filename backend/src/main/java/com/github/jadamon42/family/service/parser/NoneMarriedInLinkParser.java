package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;

public class NoneMarriedInLinkParser extends GenealogicalLinkParser {
    public NoneMarriedInLinkParser(CustomCypherQueryExecutor customCypherQueryExecutor) {
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
                case 1 -> getParentLabel(link);
                default -> "%s%s".formatted(getGreatPrefix(numberOfGenerationsToCommonAncestor - 2), getGrandParentLabel(link));
            };
        } else {
            retval = switch (numberOfGenerationsToCommonAncestor) {
                case 0 -> switch (numberOfGenerationsToOtherPerson) {
                    case -1 -> getChildLabel(link);
                    default -> "%s%s".formatted(getGreatPrefix(Math.abs(numberOfGenerationsToOtherPerson) - 2), getGrandChildLabel(link));
                };
                case 1 -> switch (numberOfGenerationsToOtherPerson) {
                    case 0 -> {
                        if (link.getSharedAncestralPartnershipId() == null) {
                            yield "Half-%s".formatted(getSiblingLabel(link));
                        }
                        yield getSiblingLabel(link);
                    }
                    default -> getFullNiblingLabel(link, numberOfGenerationsToOtherPerson);
                };
                default ->  {
                    int cousinType;
                    int timesRemoved;
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            yield getPiblingLabel(link);
                        } else if (numberOfGreats == 1) {
                            yield "Grand-%s".formatted(getPiblingLabel(link));
                        } else {
                            yield "%sGrand-%s".formatted(getGreatPrefix(numberOfGreats - 1), getPiblingLabel(link));
                        }
                    } else if (difference <= numberOfGenerationsToCommonAncestor) {
                        cousinType = difference - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    } else {
                        cousinType = numberOfGenerationsToCommonAncestor - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    }
                    yield "%sCousin%s".formatted(getCousinPrefix(cousinType), getTimeRemovedSuffix(timesRemoved));
                }
            };
        }
        return retval;
    }
}
