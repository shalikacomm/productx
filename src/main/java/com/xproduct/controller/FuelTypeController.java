package com.xproduct.controller;

import com.xproduct.dto.CreateItemTypeRequest;
import com.xproduct.dto.FuelTypeResponse;
import com.xproduct.service.FuelTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CLERK')")
    public ResponseEntity<List<FuelTypeResponse>> getAll() {
        return ResponseEntity.ok(fuelTypeService.getAllActive());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<FuelTypeResponse> create(@Valid @RequestBody CreateItemTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelTypeService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        fuelTypeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
