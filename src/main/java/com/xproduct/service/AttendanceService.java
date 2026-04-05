package com.xproduct.service;

import com.xproduct.dto.AttendanceResponse;
import com.xproduct.entity.Attendance;
import com.xproduct.entity.User;
import com.xproduct.enums.AttendanceStatus;
import com.xproduct.enums.Role;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.AttendanceRepository;
import com.xproduct.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    /** Called on attendant login — creates a PENDING attendance record */
    @Transactional
    public Attendance recordLogin(User attendant) {
        return attendanceRepository.save(
                Attendance.builder()
                        .attendant(attendant)
                        .branch(attendant.getBranch())
                        .loginTime(LocalDateTime.now())
                        .status(AttendanceStatus.PENDING)
                        .build()
        );
    }

    /** Manager approves an attendance record */
    @Transactional
    public AttendanceResponse approve(Long attendanceId, String managerUsername) {
        return updateStatus(attendanceId, managerUsername, AttendanceStatus.APPROVED);
    }

    /** Manager rejects an attendance record */
    @Transactional
    public AttendanceResponse reject(Long attendanceId, String managerUsername) {
        return updateStatus(attendanceId, managerUsername, AttendanceStatus.REJECTED);
    }

    private AttendanceResponse updateStatus(Long attendanceId, String managerUsername, AttendanceStatus newStatus) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new ApiException("Manager not found", HttpStatus.NOT_FOUND));

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ApiException("Attendance record not found", HttpStatus.NOT_FOUND));

        // Manager can only approve attendance for their own branch
        if (!attendance.getBranch().getId().equals(manager.getBranch().getId())) {
            throw new ApiException("You can only manage attendance for your own branch", HttpStatus.FORBIDDEN);
        }

        attendance.setStatus(newStatus);
        attendance.setApprovedBy(manager);
        attendance.setApprovedAt(LocalDateTime.now());

        return AttendanceResponse.from(attendanceRepository.save(attendance));
    }

    /** Get pending attendance for manager's branch */
    public List<AttendanceResponse> getPendingForManager(String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new ApiException("Manager not found", HttpStatus.NOT_FOUND));
        return attendanceRepository.findByBranchIdAndStatusOrderByLoginTimeDesc(
                        manager.getBranch().getId(), AttendanceStatus.PENDING)
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Get all attendance for manager's branch */
    public List<AttendanceResponse> getAllForManager(String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new ApiException("Manager not found", HttpStatus.NOT_FOUND));
        return attendanceRepository.findByBranchIdOrderByLoginTimeDesc(manager.getBranch().getId())
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Get attendance records for a specific attendant */
    public List<AttendanceResponse> getMyAttendance(String username) {
        User attendant = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return attendanceRepository.findByAttendantIdOrderByLoginTimeDesc(attendant.getId())
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    /** Admin: get all attendance */
    public List<AttendanceResponse> getAll() {
        return attendanceRepository.findAllByOrderByLoginTimeDesc()
                .stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }
}
