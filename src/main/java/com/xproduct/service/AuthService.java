package com.xproduct.service;

import com.xproduct.dto.LoginRequest;
import com.xproduct.dto.LoginResponse;
import com.xproduct.entity.Attendance;
import com.xproduct.entity.User;
import com.xproduct.enums.Role;
import com.xproduct.repository.UserRepository;
import com.xproduct.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        Long attendanceId = null;
        String attendanceStatus = null;

        // Auto-create attendance record for attendants
        if (user.getRole() == Role.ATTENDANT && user.getBranch() != null) {
            Attendance attendance = attendanceService.recordLogin(user);
            attendanceId = attendance.getId();
            attendanceStatus = attendance.getStatus().name();
        }

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getRole().name(),
                user.getBranch() != null ? user.getBranch().getId() : null,
                user.getBranch() != null ? user.getBranch().getName() : null,
                attendanceId,
                attendanceStatus
        );
    }
}
