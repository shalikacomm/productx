package com.xproduct.repository;

import com.xproduct.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    List<FuelType> findByActiveTrue();
    boolean existsByCode(String code);
}
