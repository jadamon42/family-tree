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
        Optional<GenealogicalLink> genealogicalLink = customCypherQueryExecutor.findLatestGenealogicalLink(personIdFrom, personIdTo);

        if (genealogicalLink.isEmpty()) {
            return Optional.empty();
        }

        GenealogicalLink link = genealogicalLink.get();
        Relation relation = link.getRelationFromPerspectiveOfPerson(personIdFrom);

        int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
        int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();

        if (numberOfGenerationsToCommonAncestor < 0 || numberOfGenerationsToOtherPerson > numberOfGenerationsToCommonAncestor) {
            return Optional.empty();
        }

        int difference = numberOfGenerationsToCommonAncestor - numberOfGenerationsToOtherPerson;
        String retval;

        if (difference == 0) {
            retval = switch (numberOfGenerationsToCommonAncestor) {
                case 0 -> "Self" ;
                case 1 -> getParentLabel(link) ;
                default -> getGreatPrefix(numberOfGenerationsToCommonAncestor - 2) + getGrantParentLabel(link);
            };
        } else {
            retval = switch (numberOfGenerationsToCommonAncestor) {
                case 0 -> switch (numberOfGenerationsToOtherPerson) {
                    case -1 -> getChildLabel(link);
                    default -> getGreatPrefix(Math.abs(numberOfGenerationsToOtherPerson) - 2) + getGrandChildLabel(link);
                };
                case 1 -> switch (numberOfGenerationsToOtherPerson) {
                    case 0 -> getSiblingLabel(link);
                    default -> {
                        int numberOfGreats = Math.abs(numberOfGenerationsToOtherPerson) - 1;
                        if (numberOfGreats == 0) {
                            yield getNiblingLabel(link);
                        } else if (numberOfGreats == 1) {
                            yield "Grand-" + getNiblingLabel(link);
                        } else {
                            yield getGreatPrefix(numberOfGreats-1) + "Grand-" + getNiblingLabel(link);
                        }
                    }
                };
                default ->  {
                    int cousinType;
                    int timesRemoved;
                    if (difference == 1) {
                        int numberOfGreats = numberOfGenerationsToCommonAncestor - 2;
                        if (numberOfGreats == 0) {
                            if (link.getPersonFromMarriedIn()) {
                                yield "%s-in-Law".formatted(getParentLabel(link));
                            } else if (link.getPersonToMarriedIn()) {
                                // if personTo's spouse an uncle, personTo is an aunt
                                // else stepparent
                                Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                                if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                    yield getPiblingLabel(link);
                                } else {
                                    yield "Step-%s".formatted(getParentLabel(link));
                                }
                            }
                            yield getPiblingLabel(link);
                        } else if (numberOfGreats == 1) {
                            if (link.getPersonFromMarriedIn()) {
                                yield "%s-in-Law".formatted(getSimpleGrantParentLabel(link));
                            } else if (link.getPersonToMarriedIn()) {
                                // if personTo's spouse a grand uncle, personTo is a grand aunt
                                // else step-grandparent
                                // TODO: refactor
                                Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                                if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                    yield "Grand-" + getPiblingLabel(link);
                                } else {
                                    yield "Step-%s".formatted(getSimpleGrantParentLabel(link));
                                }
                            }
                            yield "Grand-" + getPiblingLabel(link);
                        } else {
                            if (link.getPersonFromMarriedIn()) {
                                yield "%s-in-Law".formatted(getGreatPrefix(numberOfGenerationsToCommonAncestor - 3) + getSimpleGrantParentLabel(link));
                            } else if (link.getPersonToMarriedIn()) {
                                // if personTo's spouse a great uncle, personTo is a great aunt
                                // else step-great-grandparent
                                Optional<GenealogicalLink> spousesGenealogicalLinkViaAncestor = getSpousesGenealogicalLinkViaAncestor(link);
                                if (spousesGenealogicalLinkViaAncestor.isPresent() && isPibling(spousesGenealogicalLinkViaAncestor.get())) {
                                    yield getGreatPrefix(numberOfGreats - 1) + "Grand-" + getPiblingLabel(link);
                                } else {
                                    yield "Step-%s".formatted(getGreatPrefix(numberOfGenerationsToCommonAncestor - 3) + getSimpleGrantParentLabel(link));
                                }
                            }
                            yield getGreatPrefix(numberOfGreats - 1) + "Grand-" + getPiblingLabel(link);
                        }
                    } else if (difference <= numberOfGenerationsToCommonAncestor) {
                        if (!relation.isBloodRelation()) {
                            yield "Step-" + getSimpleSiblingLabel(link);
                        }
                        cousinType = difference - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    } else {
                        cousinType = numberOfGenerationsToCommonAncestor - 1;
                        timesRemoved = Math.abs(numberOfGenerationsToOtherPerson);
                    }
                    yield getCousinPrefix(cousinType) + "Cousin" + getTimeRemovedSuffix(timesRemoved);
                }
            };
        }

        return Optional.of(retval);
    }

    private boolean isPibling(GenealogicalLink link) {
        Relation relation = link.getRelationFromPerspectiveOfPerson(link.getPersonFromId());

        int numberOfGenerationsToCommonAncestor = relation.getNumberOfGenerationsToCommonAncestor();
        int numberOfGenerationsToOtherPerson = relation.getNumberOfGenerationsToOtherPerson();
        int difference = numberOfGenerationsToCommonAncestor - numberOfGenerationsToOtherPerson;

        return difference == 1;
    }

    private String getParentLabel(GenealogicalLink link) {
        String retval;
        if (link.getPersonToSex() == Sex.MALE) {
            retval = "Father";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            retval = "Mother" ;
        } else {
            retval = "Parent";
        }
        return retval;
    }

    private String getSimpleGrantParentLabel(GenealogicalLink link) {
        String retval;
        if (link.getPersonToSex() == Sex.MALE) {
            retval = "Grandfather" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            retval = "Grandmother" ;
        } else {
            retval = "Grandparent" ;
        }
        return retval;
    }

    private String getGrantParentLabel(GenealogicalLink link) {
        String retval = getSimpleGrantParentLabel(link);

        if (link.getPersonFromMarriedIn()) {
            retval = "Step-%s".formatted(retval);
        } else if (link.getPersonToMarriedIn()) {
            retval = "%s-in-Law".formatted(retval);
        }

        return retval;
    }

    private String getSimpleChildLabel(GenealogicalLink link) {
        String retval;
        if (link.getPersonToSex() == Sex.MALE) {
            retval = "Son" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            retval = "Daughter" ;
        } else {
            retval = "Child" ;
        }
        return retval;
    }

    private String getChildLabel(GenealogicalLink link) {
        String retval = getSimpleChildLabel(link);

        if (link.getPersonFromMarriedIn()) {
            retval = "Step-%s".formatted(retval);
        } else if (link.getPersonToMarriedIn()) {
            retval = "%s-in-Law".formatted(retval);
        }

        return retval;
    }

    private String getGrandChildLabel(GenealogicalLink link) {
        // TODO: test steps/in-Laws
        String retval;
        if (link.getPersonToSex() == Sex.MALE) {
            retval = "Grandson";
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            retval = "Granddaughter";
        } else {
            retval = "Grandchild";
        }

        if (link.getPersonFromMarriedIn()) {
            retval = "Step-%s".formatted(retval);
        } else if (link.getPersonToMarriedIn()) {
            retval = "%s-in-Law".formatted(retval);
        }

        return retval;
    }

    private String getSimpleSiblingLabel(GenealogicalLink link) {
        String retval;
        if (link.getPersonToSex() == Sex.MALE) {
            retval = "Brother" ;
        } else if (link.getPersonToSex() == Sex.FEMALE) {
            retval = "Sister" ;
        } else {
            retval = "Sibling" ;
        }
        return retval;
    }

    private String getSiblingLabel(GenealogicalLink link) {
        // TODO: have to support not marriages (not spouses)
        String retval;
        if (link.getRelationFromPerspectiveOfPerson(link.getPersonFromId()).isBloodRelation()) {
            retval = getSimpleSiblingLabel(link);
            if (link.getSharedAncestralPartnershipId() == null) {
                retval = "Half-" + retval;
            }
        } else {
            Optional<UUID> spouseId = customCypherQueryExecutor.findSpouseViaSpousesAncestor(link.getPersonToId(), link.getCommonAncestorIds().stream().findFirst().get());
            if (spouseId.isPresent() && spouseId.get().equals(link.getPersonFromId())) {
                if (link.getPersonToSex() == Sex.MALE) {
                    retval = "Husband" ;
                } else if (link.getPersonToSex() == Sex.FEMALE) {
                    retval = "Wife" ;
                } else {
                    retval = "Spouse" ;
                }
            } else {
                retval = "%s-in-Law".formatted(getSimpleSiblingLabel(link));
            }
        }
        return retval;
    }

    private String getNiblingLabel(GenealogicalLink link) {
        if (link.getPersonFromMarriedIn()) {
            return "%s-in-Law".formatted(getSimpleChildLabel(link));
        } else if (link.getPersonToMarriedIn()) {
            return "Step-%s".formatted(getSimpleChildLabel(link));
        } if (link.getPersonToSex() == Sex.MALE) {
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
