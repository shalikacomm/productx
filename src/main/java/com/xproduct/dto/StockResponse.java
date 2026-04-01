package com.xproduct.dto;

import com.xproduct.entity.Stock;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockResponse {
    private Long id;
    private Long branchId;
    private String branchName;
    private Long fuelTypeId;
    private String fuelTypeName;
    private BigDecimal quantityLiters;
    private LocalDateTime lastUpdated;

    public static StockResponse from(Stock s) {
        StockResponse r = new StockResponse();
        r.setId(s.getId());
        r.setQuantityLiters(s.getQuantityLiters());
        r.setLastUpdated(s.getLastUpdated());
        if (s.getBranch() != null) {
            r.setBranchId(s.getBranch().getId());
            r.setBranchName(s.getBranch().getName());
        }
        if (s.getFuelType() != null) {
            r.setFuelTypeId(s.getFuelType().getId());
            r.setFuelTypeName(s.getFuelType().getName());
        }
        return r;
    }
}
