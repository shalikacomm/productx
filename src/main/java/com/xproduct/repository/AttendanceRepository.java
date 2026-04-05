package com.xproduct.repository;

import com.xproduct.entity.Attendance;
import com.xproduct.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByBranchIdAndStatusOrderByLoginTimeDesc(Long branchId, AttendanceStatus status);
    List<Attendance> findByBranchIdOrderByLoginTimeDesc(Long branchId);
    List<Attendance> findByAttendantIdOrderByLoginTimeDesc(Long attendantId);
    List<Attendance> findAllByOrderByLoginTimeDesc();
}
