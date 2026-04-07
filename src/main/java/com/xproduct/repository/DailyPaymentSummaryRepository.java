package com.xproduct.repository;

import com.xproduct.entity.DailyPaymentSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyPaymentSummaryRepository extends JpaRepository<DailyPaymentSummary, Long> {

    List<DailyPaymentSummary> findByBranchIdOrderBySummaryDateDesc(Long branchId);

    List<DailyPaymentSummary> findAllByOrderBySummaryDateDesc();
}
