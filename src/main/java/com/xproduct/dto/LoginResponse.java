package com.xproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String fullName;
    private String role;
    private Long branchId;
    private String branchName;
    // Populated only for ATTENDANT role
    private Long attendanceId;
    private String attendanceStatus;
}
