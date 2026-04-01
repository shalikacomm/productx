package com.xproduct.dto;

import com.xproduct.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String fullName;

    private String email;

    private String phone;

    @NotNull
    private Role role;

    private Long branchId; // required for MANAGER, CLERK, ATTENDANT
}
