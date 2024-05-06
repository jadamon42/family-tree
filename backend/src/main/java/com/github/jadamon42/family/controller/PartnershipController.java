package com.github.jadamon42.family.controller;

import com.github.jadamon42.family.model.Partnership;
import com.github.jadamon42.family.model.PartnershipRequest;
import com.github.jadamon42.family.service.PartnershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/partnership")
@CrossOrigin(origins = "*")
public class PartnershipController {
    private final PartnershipService partnershipService;

    @Autowired
    public PartnershipController(PartnershipService partnershipService) {
        this.partnershipService = partnershipService;
    }

    @GetMapping
    public ResponseEntity<Page<Partnership>> getPartnerships(Pageable pageable) {
        return ResponseEntity.ok(partnershipService.getPartnerships(pageable));
    }

    @GetMapping("/{partnershipId}")
    public ResponseEntity<Partnership> getPartnershipById(@PathVariable UUID partnershipId) {
        return partnershipService.getPartnership(partnershipId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Partnership> addPartnership(@RequestBody @Validated PartnershipRequest request) {
        Partnership newPartnership = partnershipService.createPartnership(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(newPartnership.getId())
                                                  .toUri();
        return ResponseEntity.created(location).body(newPartnership);
    }

    @PatchMapping("/{partnershipId}")
    public ResponseEntity<Partnership> patchPartnership(@PathVariable UUID partnershipId, @RequestBody @Validated PartnershipRequest request) {
        return partnershipService.updatePartnership(partnershipId, request)
                                 .map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{partnershipId}")
    public ResponseEntity<Void> deletePartnership(@PathVariable UUID partnershipId) {
        partnershipService.deletePartnership(partnershipId);
        return ResponseEntity.noContent().build();
    }
}
