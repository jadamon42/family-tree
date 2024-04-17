package com.github.jadamon42.family.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.jadamon42.family.model.Sex;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;

public class SexConverter extends JsonDeserializer<Sex> implements Neo4jPersistentPropertyConverter<Sex> {
    @Override
    @NonNull
    public Value write(@Nullable Sex source) {
        return source == null ? Values.NULL : Values.value(source.toString());
    }

    @Override
    @NonNull
    public Sex read(@NonNull Value source) {
        return source.isNull() ? Sex.UNKNOWN : convert(source.asString());
    }

    @Override
    public Sex deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return convert(jsonParser.getText());
    }

    private Sex convert(@NonNull String source) {
        if (source.matches("(?i)m|male")) {
            return Sex.MALE;
        }
        if (source.matches("(?i)f|female")) {
            return Sex.FEMALE;
        }
        throw new IllegalArgumentException("Invalid sex: " + source + ". Expected 'Male' or 'Female'.");
    }
}
