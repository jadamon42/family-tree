package com.github.jadamon42.family.service;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GenealogicalLinkService {
    private final CustomCypherQueryExecutor customCypherQueryExecutor;

    public GenealogicalLinkService(CustomCypherQueryExecutor customCypherQueryExecutor) {
        this.customCypherQueryExecutor = customCypherQueryExecutor;
    }

    public String getRelationshipLabel(UUID personIdFrom, UUID personIdTo) {
        Optional<GenealogicalLink> genealogicalLink = customCypherQueryExecutor.findLatestGenealogicalLink(personIdFrom, personIdTo);
        return "";
    }

    // should be private. This service should return user-friendly names for the relationships
    public Optional<GenealogicalLink> getGenealogicalLink(UUID person1Id, UUID person2Id) {
        return customCypherQueryExecutor.findLatestGenealogicalLink(person1Id, person2Id);
    }
}
