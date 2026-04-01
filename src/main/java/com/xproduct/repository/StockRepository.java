package com.xproduct.repository;

import com.xproduct.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByBranchIdAndFuelTypeId(Long branchId, Long fuelTypeId);
    List<Stock> findByBranchId(Long branchId);
    List<Stock> findAllByOrderByBranchIdAscFuelTypeIdAsc();
}
