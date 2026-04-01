package com.xproduct.controller;

import com.xproduct.dto.CreateDailySaleRequest;
import com.xproduct.dto.DailySaleResponse;
import com.xproduct.service.DailySaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class DailySaleController {

    private final DailySaleService dailySaleService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DailySaleResponse> recordSale(@Valid @RequestBody CreateDailySaleRequest request,
                                                         Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailySaleService.recordSale(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<List<DailySaleResponse>> getAllSales() {
        return ResponseEntity.ok(dailySaleService.getAllSales());
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CLERK')")
    public ResponseEntity<List<DailySaleResponse>> getByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(dailySaleService.getSalesByBranch(branchId));
    }
}
