package com.xproduct.repository;

import com.xproduct.entity.Grn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GrnRepository extends JpaRepository<Grn, Long> {
    List<Grn> findByBranchIdOrderByCreatedAtDesc(Long branchId);
    List<Grn> findAllByOrderByCreatedAtDesc();
    boolean existsByGrnNumber(String grnNumber);
    List<Grn> findByReceivedDateBetweenOrderByReceivedDateDesc(LocalDate from, LocalDate to);
    List<Grn> findByBranchIdAndReceivedDateBetweenOrderByReceivedDateDesc(Long branchId, LocalDate from, LocalDate to);
}
