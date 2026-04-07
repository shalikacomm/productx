package com.xproduct.service;

import com.xproduct.dto.AttendanceResponse;
import com.xproduct.entity.Attendance;
import com.xproduct.entity.User;
import com.xproduct.enums.AttendanceStatus;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.AttendanceRepository;
import com.xproduct.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    /** Attendant clicks Clock In */
    @Transactional
    public AttendanceResponse clockIn(String username) {
        User attendant = getAttendant(username);

        // Block if there's already an open session
        Optional<Attendance> active = attendanceRepository.findActiveSession(attendant.getId());
        if (active.isPresent()) {
            throw new ApiException(
                    "You already have an active session with status: " + active.get().getStatus(),
                    HttpStatus.CONFLICT);
        }

        Attendance record = attendanceRepository.save(
                Attendance.builder()
                        .attendant(attendant)
                        .branch(attendant.getBranch())
                        .clockIn(LocalDateTime.now())
                        .status(AttendanceStatus.CLOCK_IN_PENDING)
                        .build()
        );

        return AttendanceResponse.from(record);
    }

    /** Attendant clicks Clock Out */
    @Transactional
    public AttendanceResponse clockOut(String username) {
        User attendant = getAttendant(username);

        Attendance active = attendanceRepository.findActiveSession(attendant.getId())
                .orElseThrow(() -> new ApiException("No active session found. Please clock in first.", HttpStatus.BAD_REQUEST));

        if (active.getStatus() != AttendanceStatus.CLOCKED_IN) {
            throw new ApiException(
                    "Cannot clock out. Your clock-in is still " + active.getStatus() + ". Wait for manager approval.",
                    HttpStatus.CONFLICT);
        }

        active.setClockOut(LocalDateTime.now());
        active.setStatus(AttendanceStatus.CLOCK_OUT_PENDING);

        return AttendanceResponse.from(attendanceRepository.save(active));
    }

    /** Manager approves — handles both CLOCK_IN_PENDING and CLOCK_OUT_PENDING */
    @Transactional
    public AttendanceResponse approve(Long attendanceId, String managerUsername) {
        User manager = getManager(managerUsername);
        Attendance record = getAndValidateBranch(attendanceId, manager);

        switch (record.getStatus()) {
            case CLOCK_IN_PENDING  -> record.setStatus(AttendanceStatus.CLOCKED_IN);
            case CLOCK_OUT_PENDING -> record.setStatus(AttendanceStatus.COMPLETED);
            default -> throw new ApiException("This record is not pending approval.", HttpStatus.BAD_REQUEST);
        }

        record.setApprovedBy(manager);
        record.setApprovedAt(LocalDateTime.now());

        return AttendanceResponse.from(attendanceRepository.save(record));
    }

    /** Manager rejects */
    @Transactional
    public AttendanceResponse reject(Long attendanceId, String managerUsername, String note) {
        User manager = getManager(managerUsername);
        Attendance record = getAndValidateBranch(attendanceId, manager);

        if (record.getStatus() != AttendanceStatus.CLOCK_IN_PENDING
                && record.getStatus() != AttendanceStatus.CLOCK_OUT_PENDING) {
            throw new ApiException("This record is not pending approval.", HttpStatus.BAD_REQUEST);
        }

        record.setStatus(AttendanceStatus.REJECTED);
        record.setApprovedBy(manager);
        record.setApprovedAt(LocalDateTime.now());
        record.setRejectionNote(note);

        return AttendanceResponse.from(attendanceRepository.save(record));
    }

    /** Manager: pending approvals for their branch (both clock-in and clock-out) */
    public List<AttendanceResponse> getPendingForManager(String managerUsername) {
        User manager = getManager(managerUsername);
        return attendanceRepository.findByBranchIdAndStatusInOrderByClockInDesc(
                        manager.getBranch().getId(),
                        List.of(AttendanceStatus.CLOCK_IN_PENDING, AttendanceStatus.CLOCK_OUT_PENDING))
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Manager: full attendance history for their branch */
    public List<AttendanceResponse> getAllForManager(String managerUsername) {
        User manager = getManager(managerUsername);
        return attendanceRepository.findByBranchIdOrderByClockInDesc(manager.getBranch().getId())
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Attendant: their own session status (most recent) */
    public AttendanceResponse getMyActiveSession(String username) {
        User attendant = getAttendant(username);
        return attendanceRepository.findActiveSession(attendant.getId())
                .map(AttendanceResponse::from)
                .orElse(null);
    }

    /** Attendant: full history */
    public List<AttendanceResponse> getMyAttendance(String username) {
        User attendant = getAttendant(username);
        return attendanceRepository.findByAttendantIdOrderByClockInDesc(attendant.getId())
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Admin: all records */
    public List<AttendanceResponse> getAll() {
        return attendanceRepository.findAllByOrderByClockInDesc()
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Admin/Clerk: search by date range with optional filters */
    public List<AttendanceResponse> search(String fromStr, String toStr, Long attendantId, Long branchId) {
        LocalDateTime from = LocalDate.parse(fromStr).atStartOfDay();
        LocalDateTime to   = LocalDate.parse(toStr).atTime(23, 59, 59);
        return attendanceRepository.search(from, to, attendantId, branchId)
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    // ---- Helpers ----

    private User getAttendant(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (user.getBranch() == null) {
            throw new ApiException("Attendant is not assigned to a branch", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    private User getManager(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Manager not found", HttpStatus.NOT_FOUND));
    }

    private Attendance getAndValidateBranch(Long attendanceId, User manager) {
        Attendance record = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ApiException("Attendance record not found", HttpStatus.NOT_FOUND));
        if (!record.getBranch().getId().equals(manager.getBranch().getId())) {
            throw new ApiException("You can only manage attendance for your own branch", HttpStatus.FORBIDDEN);
        }
        return record;
    }
}
