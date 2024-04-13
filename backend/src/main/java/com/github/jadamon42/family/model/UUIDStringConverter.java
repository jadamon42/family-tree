package com.github.jadamon42.family.model;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;

import java.util.UUID;

public class UUIDStringConverter implements Neo4jPersistentPropertyConverter<UUID> {

    @Override
    public Value write(UUID source) {
        return source == null ? null : Values.value(source.toString());
    }

    @Override
    public UUID read(Value source) {
        return source.isNull() ? null : UUID.fromString(source.asString());
    }
}
