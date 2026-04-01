package com.xproduct.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateGrnRequest {

    @NotNull
    private Long branchId;

    @NotNull
    private Long fuelTypeId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantityLiters;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerLiter;

    private String supplier;

    @NotNull
    private LocalDate receivedDate;

    private String notes;
}
