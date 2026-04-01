package com.xproduct.repository;

import com.xproduct.entity.Grn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrnRepository extends JpaRepository<Grn, Long> {
    List<Grn> findByBranchIdOrderByCreatedAtDesc(Long branchId);
    List<Grn> findAllByOrderByCreatedAtDesc();
    boolean existsByGrnNumber(String grnNumber);
}
