package com.xproduct.controller;

import com.xproduct.dto.FuelTypeResponse;
import com.xproduct.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
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
}
