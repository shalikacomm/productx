package com.xproduct.controller;

import com.xproduct.dto.CreateGrnRequest;
import com.xproduct.dto.GrnResponse;
import com.xproduct.service.GrnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grn")
@RequiredArgsConstructor
public class GrnController {

    private final GrnService grnService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CLERK')")
    public ResponseEntity<GrnResponse> createGrn(@Valid @RequestBody CreateGrnRequest request,
                                                  Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grnService.createGrn(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<List<GrnResponse>> getAllGrns() {
        return ResponseEntity.ok(grnService.getAllGrns());
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CLERK')")
    public ResponseEntity<List<GrnResponse>> getByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(grnService.getGrnsByBranch(branchId));
    }
}
