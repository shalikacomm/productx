package com.xproduct.repository;

import com.xproduct.entity.DailySale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailySaleRepository extends JpaRepository<DailySale, Long> {
    List<DailySale> findByBranchIdOrderBySaleDateDesc(Long branchId);
    List<DailySale> findByBranchIdAndSaleDateBetweenOrderBySaleDateDesc(Long branchId, LocalDate from, LocalDate to);
    List<DailySale> findAllByOrderBySaleDateDesc();
    List<DailySale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDate from, LocalDate to);
}
