package com.xproduct.controller;

import com.xproduct.dto.CreatePaymentSummaryRequest;
import com.xproduct.dto.PaymentSummaryResponse;
import com.xproduct.service.DailyPaymentSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-summary")
@RequiredArgsConstructor
public class DailyPaymentSummaryController {

    private final DailyPaymentSummaryService service;

    /** Manager: record daily payment summary for their branch */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PaymentSummaryResponse> record(
            @RequestBody CreatePaymentSummaryRequest request,
            Authentication auth) {
        return ResponseEntity.ok(service.record(request, auth.getName()));
    }

    /** Manager: view own branch summaries */
    @GetMapping("/branch")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<PaymentSummaryResponse>> getMyBranch(Authentication auth) {
        return ResponseEntity.ok(service.getByBranch(auth.getName()));
    }

    /** Admin/Clerk: view all summaries */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<List<PaymentSummaryResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /** Admin/Clerk: view by branch */
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<List<PaymentSummaryResponse>> getByBranchId(@PathVariable Long branchId) {
        return ResponseEntity.ok(service.getByBranchId(branchId));
    }
}
