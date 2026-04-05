package com.xproduct.repository;

import com.xproduct.entity.Attendance;
import com.xproduct.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByBranchIdAndStatusInOrderByClockInDesc(Long branchId, List<AttendanceStatus> statuses);

    List<Attendance> findByBranchIdOrderByClockInDesc(Long branchId);

    List<Attendance> findByAttendantIdOrderByClockInDesc(Long attendantId);

    List<Attendance> findAllByOrderByClockInDesc();

    /** Find the most recent open session for an attendant (not COMPLETED or REJECTED) */
    @Query("SELECT a FROM Attendance a WHERE a.attendant.id = :attendantId " +
           "AND a.status NOT IN ('COMPLETED', 'REJECTED') ORDER BY a.clockIn DESC")
    Optional<Attendance> findActiveSession(@Param("attendantId") Long attendantId);
}
