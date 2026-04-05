package com.xproduct.dto;

import com.xproduct.entity.DailySale;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailySaleResponse {
    private Long id;
    private LocalDate saleDate;
    private Long branchId;
    private String branchName;
    private Long fuelTypeId;
    private String fuelTypeName;
    private String fuelTypeUnit;
    private BigDecimal litersSold;
    private BigDecimal pricePerLiter;
    private BigDecimal totalAmount;
    private String recordedByUsername;
    private LocalDateTime createdAt;

    public static DailySaleResponse from(DailySale s) {
        DailySaleResponse r = new DailySaleResponse();
        r.setId(s.getId());
        r.setSaleDate(s.getSaleDate());
        r.setLitersSold(s.getLitersSold());
        r.setPricePerLiter(s.getPricePerLiter());
        r.setTotalAmount(s.getTotalAmount());
        r.setCreatedAt(s.getCreatedAt());
        if (s.getBranch() != null) {
            r.setBranchId(s.getBranch().getId());
            r.setBranchName(s.getBranch().getName());
        }
        if (s.getFuelType() != null) {
            r.setFuelTypeId(s.getFuelType().getId());
            r.setFuelTypeName(s.getFuelType().getName());
            r.setFuelTypeUnit(s.getFuelType().getUnit());
        }
        if (s.getRecordedBy() != null) {
            r.setRecordedByUsername(s.getRecordedBy().getUsername());
        }
        return r;
    }
}
