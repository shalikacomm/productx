package com.xproduct.repository;

import com.xproduct.entity.Attendance;
import com.xproduct.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    /** Search by date range with optional attendant and branch filters */
    @Query("SELECT a FROM Attendance a WHERE " +
           "a.clockIn >= :from AND a.clockIn <= :to " +
           "AND (:attendantId IS NULL OR a.attendant.id = :attendantId) " +
           "AND (:branchId IS NULL OR a.branch.id = :branchId) " +
           "ORDER BY a.attendant.fullName ASC, a.clockIn DESC")
    List<Attendance> search(@Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            @Param("attendantId") Long attendantId,
                            @Param("branchId") Long branchId);
}
