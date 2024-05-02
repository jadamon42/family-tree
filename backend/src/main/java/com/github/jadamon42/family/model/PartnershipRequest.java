package com.github.jadamon42.family.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnershipRequest {
    Optional<String> type;
    Optional<LocalDate> startDate;
    Optional<LocalDate> endDate;
    @Builder.Default
    List<UUID> partnerIds = List.of();

    @JsonCreator
    public static PartnershipRequest create(
            Optional<String> type,
            Optional<LocalDate> startDate,
            Optional<LocalDate> endDate,
            List<UUID> partnerIds) {
        return PartnershipRequest.builder()
                                 .type(type)
                                 .startDate(startDate)
                                 .endDate(endDate)
                                 .partnerIds(partnerIds != null ? partnerIds : List.of())
                                 .build();
    }
}
