package com.xproduct.dto;

import com.xproduct.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Long branchId;
    private String branchName;
    private String createdByUsername;
    private Boolean active;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setFullName(user.getFullName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setRole(user.getRole().name());
        r.setActive(user.getActive());
        r.setCreatedAt(user.getCreatedAt());
        if (user.getBranch() != null) {
            r.setBranchId(user.getBranch().getId());
            r.setBranchName(user.getBranch().getName());
        }
        if (user.getCreatedBy() != null) {
            r.setCreatedByUsername(user.getCreatedBy().getUsername());
        }
        return r;
    }
}
