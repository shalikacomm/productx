package com.xproduct.controller;

import com.xproduct.dto.ReportResponse;
import com.xproduct.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * GET /api/reports?type=daily&date=2026-04-05&branchId=1
     * GET /api/reports?type=weekly&date=2026-04-01&branchId=
     * GET /api/reports?type=monthly&year=2026&month=4&branchId=
     * GET /api/reports?type=yearly&year=2026&branchId=
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReport(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long branchId) {

        LocalDate from, to;
        String label;
        LocalDate today = LocalDate.now();

        switch (type) {
            case "daily" -> {
                LocalDate d = date != null ? date : today;
                from = d; to = d;
                label = "Daily — " + d.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            }
            case "weekly" -> {
                LocalDate start = date != null ? date : today.minusDays(today.getDayOfWeek().getValue() - 1);
                from = start; to = start.plusDays(6);
                label = "Weekly — " + from.format(DateTimeFormatter.ofPattern("dd MMM")) + " to " + to.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            }
            case "monthly" -> {
                int y = year != null ? year : today.getYear();
                int m = month != null ? month : today.getMonthValue();
                from = LocalDate.of(y, m, 1);
                to   = from.withDayOfMonth(from.lengthOfMonth());
                label = "Monthly — " + from.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            }
            case "yearly" -> {
                int y = year != null ? year : today.getYear();
                from = LocalDate.of(y, 1, 1);
                to   = LocalDate.of(y, 12, 31);
                label = "Yearly — " + y;
            }
            default -> {
                from = today; to = today;
                label = "Daily — " + today;
            }
        }

        return ResponseEntity.ok(reportService.generate(from, to, branchId, label));
    }
}
