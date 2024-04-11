package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipProjection;
import com.github.jadamon42.family.service.PartnershipService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/partnership")
public class PartnershipController {
    private final PartnershipService partnershipService;

    @Autowired
    public PartnershipController(PartnershipService partnershipService) {
        this.partnershipService = partnershipService;
    }

    @GetMapping("/{partnershipId}")
    public ResponseEntity<Partnership> getPartnershipById(@PathVariable UUID partnershipId) {
        return partnershipService.getPartnership(partnershipId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PartnershipProjection> addPartnership(@RequestBody @Validated PartnershipRequest partnershipRequest) {
        PartnershipProjection newPartnership = partnershipService.savePartnership(partnershipRequest.partnership, partnershipRequest.partnerIds);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(newPartnership.getId())
                                                  .toUri();
        return ResponseEntity.created(location).body(newPartnership);
    }

    @PatchMapping("/{partnershipId}")
    public ResponseEntity<PartnershipProjection> patchPartnership(@PathVariable UUID partnershipId, @RequestBody @Validated PartnershipRequest partnershipRequest) {
        return partnershipService.updatePartnership(partnershipId, partnershipRequest.partnership, partnershipRequest.partnerIds)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{partnershipId}")
    public ResponseEntity<Void> deletePartnership(@PathVariable UUID partnershipId) {
        partnershipService.deletePartnership(partnershipId);
        return ResponseEntity.noContent().build();
    }

    public record PartnershipRequest(
            @NotNull Partnership partnership,
            @NotEmpty List<UUID> partnerIds) {
    }
}
