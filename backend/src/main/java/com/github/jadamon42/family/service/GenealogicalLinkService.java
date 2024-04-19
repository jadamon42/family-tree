package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.model.Relation;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import com.github.jadamon42.family.service.parser.GenealogicalLinkParser;
import com.github.jadamon42.family.service.parser.GenealogicalLinkParserFactory;
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
                GenealogicalLinkParser parser = GenealogicalLinkParserFactory.getParser(link, customCypherQueryExecutor);
                retval = parser.getLabel(link, numberOfGenerationsToCommonAncestor, numberOfGenerationsToOtherPerson);
            }
        }

        return Optional.ofNullable(retval);
    }
}
