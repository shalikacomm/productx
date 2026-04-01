package com.xproduct.service;

import com.xproduct.dto.BranchResponse;
import com.xproduct.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponse> getAllActiveBranches() {
        return branchRepository.findByActiveTrue()
                .stream()
                .map(BranchResponse::from)
                .collect(Collectors.toList());
    }
}
