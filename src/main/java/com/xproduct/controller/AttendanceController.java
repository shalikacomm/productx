package com.xproduct.controller;

import com.xproduct.dto.AttendanceResponse;
import com.xproduct.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** Admin/Clerk: all attendance records */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<List<AttendanceResponse>> getAll() {
        return ResponseEntity.ok(attendanceService.getAll());
    }

    /** Manager: pending approvals for their branch */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> getPending(Authentication auth) {
        // BranchId resolved in service from manager's profile
        return ResponseEntity.ok(attendanceService.getPendingForManager(auth.getName()));
    }

    /** Manager: all attendance for their branch */
    @GetMapping("/branch")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> getBranchAttendance(Authentication auth) {
        return ResponseEntity.ok(attendanceService.getAllForManager(auth.getName()));
    }

    /** Manager: approve */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AttendanceResponse> approve(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(attendanceService.approve(id, auth.getName()));
    }

    /** Manager: reject */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AttendanceResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth) {
        String note = body != null ? body.getOrDefault("note", "") : "";
        return ResponseEntity.ok(attendanceService.reject(id, auth.getName(), note));
    }

    /** Attendant: clock in */
    @PostMapping("/clock-in")
    @PreAuthorize("hasRole('ATTENDANT')")
    public ResponseEntity<AttendanceResponse> clockIn(Authentication auth) {
        return ResponseEntity.ok(attendanceService.clockIn(auth.getName()));
    }

    /** Attendant: clock out */
    @PutMapping("/clock-out")
    @PreAuthorize("hasRole('ATTENDANT')")
    public ResponseEntity<AttendanceResponse> clockOut(Authentication auth) {
        return ResponseEntity.ok(attendanceService.clockOut(auth.getName()));
    }

    /** Attendant: current active session */
    @GetMapping("/my/active")
    @PreAuthorize("hasRole('ATTENDANT')")
    public ResponseEntity<AttendanceResponse> getMyActiveSession(Authentication auth) {
        AttendanceResponse res = attendanceService.getMyActiveSession(auth.getName());
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.noContent().build();
    }

    /** Attendant: their own attendance history */
    @GetMapping("/my")
    @PreAuthorize("hasRole('ATTENDANT')")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(Authentication auth) {
        return ResponseEntity.ok(attendanceService.getMyAttendance(auth.getName()));
    }
}
