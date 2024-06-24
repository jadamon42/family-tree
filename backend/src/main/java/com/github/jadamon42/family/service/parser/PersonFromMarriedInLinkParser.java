package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;

import java.util.Optional;

public class PersonFromMarriedInLinkParser extends GenealogicalLinkParser {
    public PersonFromMarriedInLinkParser(CustomCypherQueryExecutor customCypherQueryExecutor) {
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
                case 1 -> "%s-in-Law".formatted(getParentLabel(link));
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
                    default -> getStepChildOrNiblingLabel(link, numberOfGenerationsToOtherPerson);
                };
                default ->  {
                    int cousinType;
                    int timesRemoved;
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            if (link.getCommonAncestorIds().size() == 1) {
                                yield "Step-%s".formatted(getParentLabel(link));
                            } else {
                                yield "%s-in-Law".formatted(getPiblingLabel(link));
                            }
                        } else if (numberOfGreats == 1) {
                            yield "Grand-%s-in-Law".formatted(getPiblingLabel(link));
                        } else {
                            yield "%sGrand-%s-in-Law".formatted(getGreatPrefix(numberOfGreats - 1), getPiblingLabel(link));
                        }
                    } else if (difference <= numberOfGenerationsToCommonAncestor) {
                        if (link.getCommonAncestorIds().size() == 1) {
                            yield "Step-%s".formatted(getSiblingLabel(link));
                        } else {
                            cousinType = difference - 1;
                            timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                            yield "%sCousin-in-Law%s".formatted(getCousinPrefix(cousinType), getTimeRemovedSuffix(timesRemoved));
                        }
                    } else {
                        cousinType = numberOfGenerationsToCommonAncestor - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                        yield "%sCousin-in-Law%s".formatted(getCousinPrefix(cousinType), getTimeRemovedSuffix(timesRemoved));
                    }
                }
            };
        }
        return retval;
    }

    private String getStepChildOrNiblingLabel(GenealogicalLink link, int numberOfGenerationsToOtherPerson) {
        Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link.getInverse());
        if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
            return getFullNiblingLabel(link, numberOfGenerationsToOtherPerson);
        } else {
            if (numberOfGenerationsToOtherPerson == -1) {
                return "Step-%s".formatted(getChildLabel(link));
            } else {
                return "Step-%s%s".formatted(getGreatPrefix(Math.abs(numberOfGenerationsToOtherPerson) - 2), getGrandChildLabel(link));
            }
        }
    }
}
