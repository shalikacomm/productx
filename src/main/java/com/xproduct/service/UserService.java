package com.xproduct.service;

import com.xproduct.dto.CreateUserRequest;
import com.xproduct.dto.UserResponse;
import com.xproduct.entity.Branch;
import com.xproduct.entity.User;
import com.xproduct.enums.Role;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.BranchRepository;
import com.xproduct.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user. Enforces role-based creation rules:
     * - ADMIN can create MANAGER, CLERK, ATTENDANT
     * - MANAGER can only create ATTENDANT
     */
    public UserResponse createUser(CreateUserRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new ApiException("Creator not found", HttpStatus.NOT_FOUND));

        validateCreationPermission(creator.getRole(), request.getRole());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already exists", HttpStatus.CONFLICT);
        }

        Branch branch = null;
        if (request.getRole() != Role.ADMIN) {
            if (request.getBranchId() == null) {
                throw new ApiException("Branch is required for " + request.getRole(), HttpStatus.BAD_REQUEST);
            }
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ApiException("Branch not found", HttpStatus.NOT_FOUND));
        }

        // MANAGER must be assigned to creator's branch if creator is MANAGER
        if (creator.getRole() == Role.MANAGER && branch != null) {
            if (!branch.getId().equals(creator.getBranch().getId())) {
                throw new ApiException("Managers can only create attendants in their own branch", HttpStatus.FORBIDDEN);
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .branch(branch)
                .createdBy(creator)
                .active(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findByActiveTrue()
                .stream().map(UserResponse::from).collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRoleAndActiveTrue(role)
                .stream().map(UserResponse::from).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    public void deactivateUser(Long id, String requestorUsername) {
        User requestor = userRepository.findByUsername(requestorUsername)
                .orElseThrow(() -> new ApiException("Requestor not found", HttpStatus.NOT_FOUND));
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (target.getRole() == Role.ADMIN) {
            throw new ApiException("Cannot deactivate admin users", HttpStatus.FORBIDDEN);
        }

        if (requestor.getRole() == Role.MANAGER && target.getRole() != Role.ATTENDANT) {
            throw new ApiException("Managers can only deactivate attendants", HttpStatus.FORBIDDEN);
        }

        target.setActive(false);
        userRepository.save(target);
    }

    private void validateCreationPermission(Role creatorRole, Role targetRole) {
        switch (creatorRole) {
            case ADMIN -> {
                if (targetRole == Role.ADMIN) {
                    throw new ApiException("Cannot create another admin", HttpStatus.FORBIDDEN);
                }
            }
            case MANAGER -> {
                if (targetRole != Role.ATTENDANT) {
                    throw new ApiException("Managers can only create attendants", HttpStatus.FORBIDDEN);
                }
            }
            default -> throw new ApiException("You do not have permission to create users", HttpStatus.FORBIDDEN);
        }
    }
}
