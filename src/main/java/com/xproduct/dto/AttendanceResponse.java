package com.xproduct.dto;

import com.xproduct.entity.Attendance;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceResponse {
    private Long id;
    private Long attendantId;
    private String attendantName;
    private String attendantUsername;
    private Long branchId;
    private String branchName;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String status;
    private String approvedByUsername;
    private LocalDateTime approvedAt;

    public static AttendanceResponse from(Attendance a) {
        AttendanceResponse r = new AttendanceResponse();
        r.setId(a.getId());
        r.setLoginTime(a.getLoginTime());
        r.setLogoutTime(a.getLogoutTime());
        r.setStatus(a.getStatus().name());
        r.setApprovedAt(a.getApprovedAt());
        if (a.getAttendant() != null) {
            r.setAttendantId(a.getAttendant().getId());
            r.setAttendantName(a.getAttendant().getFullName());
            r.setAttendantUsername(a.getAttendant().getUsername());
        }
        if (a.getBranch() != null) {
            r.setBranchId(a.getBranch().getId());
            r.setBranchName(a.getBranch().getName());
        }
        if (a.getApprovedBy() != null) {
            r.setApprovedByUsername(a.getApprovedBy().getUsername());
        }
        return r;
    }
}
