package com.github.jadamon42.family.util;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class UUIDStringConverter implements Neo4jPersistentPropertyConverter<UUID> {

    @Override
    @NonNull
    public Value write(@Nullable UUID source) {
        return source == null ? Values.NULL : Values.value(source.toString());
    }

    @Override
    @Nullable
    public UUID read(@NonNull Value source) {
        return source.isNull() ? null : UUID.fromString(source.asString());
    }
}
