package com.github.jadamon42.family.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Optional;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRequest {
    Optional<String> firstName;
    Optional<String> lastName;

    public static class PersonRequestBuilder {
        @JsonAnySetter
        public PersonRequestBuilder unknown(String name, Object ignoredValue) {
            throw new IllegalArgumentException("Unknown property: " + name);
        }
    }
}
