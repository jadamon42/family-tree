package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.model.Sex;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import com.github.jadamon42.family.util.Ordinal;

import java.util.Optional;
import java.util.UUID;

public abstract class GenealogicalLinkParser {
    private final CustomCypherQueryExecutor customCypherQueryExecutor;

    public GenealogicalLinkParser(CustomCypherQueryExecutor customCypherQueryExecutor) {
        this.customCypherQueryExecutor = customCypherQueryExecutor;
    }

    public abstract String getLabel(GenealogicalLink link);

    String getFullNiblingLabel(GenealogicalLink link, int numberOfGenerationsToOtherPerson) {
        int numberOfGreats = Math.abs(numberOfGenerationsToOtherPerson) - 1;
        if (numberOfGreats == 0) {
            return getNiblingLabel(link);
        } else if (numberOfGreats == 1) {
            return "Grand-%s".formatted(getNiblingLabel(link));
        } else {
            return "%sGrand-%s".formatted(getGreatPrefix(numberOfGreats - 1), getNiblingLabel(link));
        }
    }

    String getSpouseOrSiblingInLawLabel(GenealogicalLink link) {
        boolean isSpouse = customCypherQueryExecutor.isSpouse(link.getPersonFromId(), link.getPersonToId());
        if (isSpouse) {
            return getSpouseLabel(link);
        } else if (link.getCommonAncestorIds().size() == 1) {
            return "Half-%s-in-Law".formatted(getSiblingLabel(link));
        }
        return "%s-in-Law".formatted(getSiblingLabel(link));
    }

    boolean isPibling(GenealogicalLink link) {
        Relation relation = link.getRelationFromPerspectiveOfPersonFrom();

        int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
        int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();
        int difference = numberOfGenerationsToCommonAncestor - numberOfGenerationsToOtherPerson;

        return difference == 1;
    }

    String getParentLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Father";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Mother" ;
        }
        return "Parent";
    }

    String getGrandParentLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Grandfather" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Grandmother" ;
        }
        return "Grandparent" ;
    }

    String getChildLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Son" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Daughter" ;
        }
        return "Child" ;
    }

    String getGrandChildLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Grandson";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Granddaughter";
        }
        return "Grandchild";
    }

    String getSiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Brother" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Sister" ;
        }
        return "Sibling" ;
    }

    String getSpouseLabel(GenealogicalLink link) {
        // TODO: have to support not marriages (not spouses)
        if (link.getPersonToSex() == Sex.MALE) {
            return "Husband" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Wife" ;
        }
        return "Spouse" ;
    }

    String getNiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Nephew";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Niece";
        }
        return "Nibling";
    }

    String getPiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Uncle";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Aunt";
        }
        return "Pibling";
    }

    String getGreatPrefix(int numberOfGreats) {
        if (numberOfGreats  <= 2) {
            return "Great-".repeat(numberOfGreats);
        }
        return "%sx Great-".formatted(Ordinal.of(numberOfGreats));
    }

    String getTimeRemovedSuffix(int timesRemoved) {
        if (timesRemoved == 0) {
            return "";
        } else if (timesRemoved == 1) {
            return " Once Removed";
        } else if (timesRemoved == 2) {
            return " Twice Removed";
        } else if (timesRemoved == 3) {
            return " Thrice Removed";
        }
        return " %sx Removed".formatted(timesRemoved);
    }

    String getCousinPrefix(int cousinType) {
        if (cousinType == 0) {
            return "";
        }
        return "%s ".formatted(Ordinal.of(cousinType));
    }

    Optional<GenealogicalLink> getSpousesGenealogicalLinkViaAncestor(GenealogicalLink link) {
        UUID ancestorId = link.getCommonAncestorIds().stream().findFirst().get();
        Optional<UUID> spouseId = customCypherQueryExecutor.findSpouseViaSpousesAncestor(link.getPersonToId(), ancestorId);
        if (spouseId.isEmpty()) {
            return Optional.empty();
        }
        return customCypherQueryExecutor.findLatestGenealogicalLink(link.getPersonFromId(), spouseId.get());
    }
}
