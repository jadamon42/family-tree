package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.model.Sex;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import com.github.jadamon42.family.util.Ordinal;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GenealogicalLinkService {
    private final CustomCypherQueryExecutor customCypherQueryExecutor;

    public GenealogicalLinkService(CustomCypherQueryExecutor customCypherQueryExecutor) {
        this.customCypherQueryExecutor = customCypherQueryExecutor;
    }

    public Optional<String> getRelationshipLabel(UUID personIdFrom, UUID personIdTo) {
        String retval = null;
        Optional<GenealogicalLink> genealogicalLink = customCypherQueryExecutor.findLatestGenealogicalLink(personIdFrom, personIdTo);

        if (genealogicalLink.isPresent()) {
            GenealogicalLink link = genealogicalLink.get();
            Relation relation = link.getRelationFromPerspectiveOfPersonFrom();

            int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
            int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();
            boolean isValidRelation = numberOfGenerationsToCommonAncestor >= 0
                                                    && numberOfGenerationsToOtherPerson <= numberOfGenerationsToCommonAncestor;

            if (isValidRelation) {
                if (link.getPersonFromMarriedIn() && link.getPersonToMarriedIn()) {
                    retval = getRelationshipLabelBothMarriedIn(
                            link, numberOfGenerationsToCommonAncestor, numberOfGenerationsToOtherPerson);
                } else if (link.getPersonFromMarriedIn()) {
                    retval = getRelationshipLabelPersonFromMarriedIn(
                            link, numberOfGenerationsToCommonAncestor, numberOfGenerationsToOtherPerson);
                } else if (link.getPersonToMarriedIn()) {
                    retval = getRelationshipLabelPersonToMarriedIn(
                            link, numberOfGenerationsToCommonAncestor, numberOfGenerationsToOtherPerson);
                } else {
                    retval = getRelationshipLabelNoneMarriedIn(
                            link, numberOfGenerationsToCommonAncestor, numberOfGenerationsToOtherPerson);
                }
            }
        }

        return Optional.ofNullable(retval);
    }

    private String getRelationshipLabelBothMarriedIn(GenealogicalLink link, int numberOfGenerationsToCommonAncestor, int numberOfGenerationsToOtherPerson) {
        String retval;
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

    private String getRelationshipLabelPersonFromMarriedIn(GenealogicalLink link, int numberOfGenerationsToCommonAncestor, int numberOfGenerationsToOtherPerson) {
        String retval;
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
                    default -> getFullNiblingLabel(link, numberOfGenerationsToOtherPerson);
                };
                default ->  {
                    int cousinType;
                    int timesRemoved;
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            yield "%s-in-Law".formatted(getPiblingLabel(link));
                        } else if (numberOfGreats == 1) {
                            yield "Grand-%s-in-Law".formatted(getPiblingLabel(link));
                        } else {
                            yield "%sGrand-%s-in-Law".formatted(getGreatPrefix(numberOfGreats - 1), getPiblingLabel(link));
                        }
                    } else if (difference <= numberOfGenerationsToCommonAncestor) {
                        cousinType = difference - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    } else {
                        cousinType = numberOfGenerationsToCommonAncestor - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    }
                    yield "%sCousin-in-Law%s".formatted(getCousinPrefix(cousinType), getTimeRemovedSuffix(timesRemoved));
                }
            };
        }
        return retval;
    }

    private String getRelationshipLabelPersonToMarriedIn(GenealogicalLink link, int numberOfGenerationsToCommonAncestor, int numberOfGenerationsToOtherPerson) {
        String retval;
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
                    default -> "Step-%s".formatted(getChildLabel(link));
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
                    yield "Step-%s".formatted(getSiblingLabel(link));
                }
            };
        }
        return retval;
    }

    public String getRelationshipLabelNoneMarriedIn(GenealogicalLink link, int numberOfGenerationsToCommonAncestor, int numberOfGenerationsToOtherPerson) {
        String retval;
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

    private String getFullNiblingLabel(GenealogicalLink link, int numberOfGenerationsToOtherPerson) {
        int numberOfGreats = Math.abs(numberOfGenerationsToOtherPerson) - 1;
        if (numberOfGreats == 0) {
            return getNiblingLabel(link);
        } else if (numberOfGreats == 1) {
            return "Grand-%s".formatted(getNiblingLabel(link));
        } else {
            return "%sGrand-%s".formatted(getGreatPrefix(numberOfGreats - 1), getNiblingLabel(link));
        }
    }

    private String getSpouseOrSiblingInLawLabel(GenealogicalLink link) {
        Optional<UUID> spouseId = customCypherQueryExecutor.findSpouseViaSpousesAncestor(link.getPersonToId(), link.getCommonAncestorIds().stream().findFirst().get());
        if (spouseId.isPresent() && spouseId.get().equals(link.getPersonFromId())) {
            return getSpouseLabel(link);
        }
        return "%s-in-Law".formatted(getSiblingLabel(link));
    }

    private boolean isPibling(GenealogicalLink link) {
        Relation relation = link.getRelationFromPerspectiveOfPersonFrom();

        int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
        int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();
        int difference = numberOfGenerationsToCommonAncestor - numberOfGenerationsToOtherPerson;

        return difference == 1;
    }

    private String getParentLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Father";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Mother" ;
        }
        return "Parent";
    }

    private String getGrandParentLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Grandfather" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Grandmother" ;
        }
        return "Grandparent" ;
    }

    private String getChildLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Son" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Daughter" ;
        }
        return "Child" ;
    }

    private String getGrandChildLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Grandson";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Granddaughter";
        }
        return "Grandchild";
    }

    private String getSiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Brother" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Sister" ;
        }
        return "Sibling" ;
    }

    private String getSpouseLabel(GenealogicalLink link) {
        // TODO: have to support not marriages (not spouses)
        if (link.getPersonToSex() == Sex.MALE) {
            return "Husband" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Wife" ;
        }
        return "Spouse" ;
    }

    private String getNiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Nephew";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Niece";
        }
        return "Nibling";
    }

    private String getPiblingLabel(GenealogicalLink link) {
        if (link.getPersonToSex() == Sex.MALE) {
            return "Uncle";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            return "Aunt";
        }
        return "Pibling";
    }

    private String getGreatPrefix(int numberOfGreats) {
        if (numberOfGreats  <= 2) {
            return "Great-".repeat(numberOfGreats);
        }
        return "%sx Great-".formatted(Ordinal.of(numberOfGreats));
    }

    private String getTimeRemovedSuffix(int timesRemoved) {
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

    private String getCousinPrefix(int cousinType) {
        if (cousinType == 0) {
            return "";
        }
        return "%s ".formatted(Ordinal.of(cousinType));
    }

    private Optional<GenealogicalLink> getSpousesGenealogicalLinkViaAncestor(GenealogicalLink link) {
        UUID ancestorId = link.getCommonAncestorIds().stream().findFirst().get();
        Optional<UUID> spouseId = customCypherQueryExecutor.findSpouseViaSpousesAncestor(link.getPersonToId(), ancestorId);
        if (spouseId.isEmpty()) {
            return Optional.empty();
        }
        return customCypherQueryExecutor.findLatestGenealogicalLink(link.getPersonFromId(), spouseId.get());
    }
}
