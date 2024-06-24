package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;

import java.util.Optional;

public class PersonToMarriedInLinkParser extends GenealogicalLinkParser {
    public PersonToMarriedInLinkParser(CustomCypherQueryExecutor customCypherQueryExecutor) {
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
                    case -1 -> "%s-in-Law".formatted(getChildLabel(link));
                    default -> "%s%s-in-Law".formatted(getGreatPrefix(Math.abs(numberOfGenerationsToOtherPerson) - 2), getGrandChildLabel(link));
                };
                case 1 -> switch (numberOfGenerationsToOtherPerson) {
                    case 0 -> getSpouseOrSiblingInLawLabel(link);
                    default -> getStepChildOrPiblingInLawLabel(link, numberOfGenerationsToOtherPerson);
                };
                default ->  {
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                            if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                yield getPiblingLabel(link);
                            } else {
                                yield "Step-%s".formatted(getParentLabel(link));
                            }
                        } else if (numberOfGreats == 1) {
                            Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                            if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                yield "Grand-%s".formatted(getPiblingLabel(link));
                            } else {
                                yield "Step-%s".formatted(getGrandParentLabel(link));
                            }
                        } else {
                            Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                            if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                yield "%sGrand-%s".formatted(getGreatPrefix(numberOfGreats - 1), getPiblingLabel(link));
                            } else {
                                yield "Step-%s%s".formatted(getGreatPrefix(numberOfGreats - 1), getGrandParentLabel(link));
                            }
                        }
                    }
                    if (link.getCommonAncestorIds().size() == 1) {
                        yield "Step-%s".formatted(getSiblingLabel(link));
                    } else {
                        int cousinType;
                        int timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                        if (difference <= numberOfGenerationsToCommonAncestor) {
                            cousinType = difference - 1;
                        } else {
                            cousinType = numberOfGenerationsToCommonAncestor - 1;
                        }
                        yield "%sCousin-in-Law%s".formatted(getCousinPrefix(cousinType), getTimeRemovedSuffix(timesRemoved));
                    }
                }
            };
        }
        return retval;
    }

    private String getStepChildOrPiblingInLawLabel(GenealogicalLink link, int numberOfGenerationsToOtherPerson) {
        // this can't be right. Can you have an in-law when you only have 1 common ancestor?
        // can you have a stepparent with 2 common ancestors?
        if (link.getCommonAncestorIds().size() == 1) {
            return "Step-%s".formatted(getChildLabel(link));
        } else {
            return "%s-in-Law".formatted(getFullNiblingLabel(link, numberOfGenerationsToOtherPerson));
        }
    }
}
