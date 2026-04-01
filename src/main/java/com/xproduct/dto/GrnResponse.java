package com.xproduct.dto;

import com.xproduct.entity.Grn;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GrnResponse {
    private Long id;
    private String grnNumber;
    private Long branchId;
    private String branchName;
    private Long fuelTypeId;
    private String fuelTypeName;
    private BigDecimal quantityLiters;
    private BigDecimal pricePerLiter;
    private BigDecimal totalAmount;
    private String supplier;
    private LocalDate receivedDate;
    private String notes;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public static GrnResponse from(Grn g) {
        GrnResponse r = new GrnResponse();
        r.setId(g.getId());
        r.setGrnNumber(g.getGrnNumber());
        r.setQuantityLiters(g.getQuantityLiters());
        r.setPricePerLiter(g.getPricePerLiter());
        r.setTotalAmount(g.getTotalAmount());
        r.setSupplier(g.getSupplier());
        r.setReceivedDate(g.getReceivedDate());
        r.setNotes(g.getNotes());
        r.setCreatedAt(g.getCreatedAt());
        if (g.getBranch() != null) {
            r.setBranchId(g.getBranch().getId());
            r.setBranchName(g.getBranch().getName());
        }
        if (g.getFuelType() != null) {
            r.setFuelTypeId(g.getFuelType().getId());
            r.setFuelTypeName(g.getFuelType().getName());
        }
        if (g.getCreatedBy() != null) {
            r.setCreatedByUsername(g.getCreatedBy().getUsername());
        }
        return r;
    }
}
